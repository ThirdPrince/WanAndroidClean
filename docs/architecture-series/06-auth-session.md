# 06. 基础设施：登录、Cookie、收藏为什么属于外层细节

Clean Architecture 会把数据库、网络、Cookie、缓存、系统 API 都视为外层细节。玩 Android 的登录态依赖 Cookie，WanAndroidClean 没有把 Cookie 处理散落在登录页面，而是放进 OkHttp 和 DataStore 组成的基础设施里，这一点很符合 Clean 思路。

## 登录调用链

登录流程大致是：

```text
LoginScreen
  -> LoginViewModel.login(username, password)
  -> LoginUseCase
  -> UserRepository.login()
  -> WanAndroidApi.login()
  -> safeApiCall
  -> OkHttp 接收 Set-Cookie
  -> PersistentCookieJar.saveFromResponse()
  -> CookieStorage.saveCookies()
  -> DataStore 持久化
```

LoginViewModel 本身只关心加载、成功、失败，不直接读写 Cookie。`domain` 也不需要知道 Cookie 如何保存，它只暴露 `UserRepository.login()` 和登录状态能力。

这条链路体现了 Clean 架构的分工：

```text
表现层：
  收集用户名密码，展示 loading/error/success

领域层：
  定义登录能力和登录用例

数据层：
  调用登录接口，处理 Result，保存 Cookie

基础设施：
  OkHttp CookieJar + DataStore
```

登录是业务动作，但 Cookie 是实现细节。把 Cookie 存储放到 `data.remote` 相关基础设施里，比放在 LoginViewModel 里更合理。

## CookieStorage 的设计

`CookieStorage` 同时维护两份数据：

- 内存中的 `ConcurrentHashMap<String, List<Cookie>>`
- DataStore 中的持久化字符串

内存缓存用于 `loadForRequest()` 快速返回 Cookie。DataStore 用于 App 重启后恢复登录态。

初始化时，`CookieStorage` 会从 DataStore 读取已有 Cookie，解析后放入内存缓存。保存 Cookie 时，会先更新内存，再异步写入 DataStore。

这套设计解决了两个问题：

第一，请求时需要快速拿到 Cookie。如果每次请求都阻塞读取 DataStore，会影响网络请求链路。

第二，应用重启后需要恢复 Cookie。仅靠内存缓存无法保留登录态，所以需要持久化。

因此它采用“内存缓存 + DataStore 持久化”的组合。内存负责运行时效率，DataStore 负责跨进程生命周期保存。

## 登录状态如何判断

`UserRepositoryImpl` 暴露：

```kotlin
override val isUserLoggedIn: Flow<Boolean> = cookieStorage.cookies.map { cookies ->
    cookies.any { it.name == "loginUserName" && it.value.isNotEmpty() }
}
```

这是一种轻量判断方式：只要持久化 Cookie 中存在 `loginUserName`，页面就认为用户已登录。判断细节留在 `data` 层，表现层只消费登录状态。

它的优点是简单、响应式、无需额外请求。缺点是无法确认服务端会话是否仍有效。更严谨的方案是在关键入口调用用户信息接口校验，失败时清理 Cookie。

这里也可以看出 Repository 接口的意义。表现层不需要知道登录态是从 Cookie 推导、token 推导，还是用户信息接口校验。它只需要消费：

```kotlin
val isUserLoggedIn: Flow<Boolean>
```

以后如果登录态判断从 Cookie 改为 token，或增加服务端校验，改动应该集中在 `UserRepositoryImpl` 和相关基础设施中。

## 收藏动作

收藏接口位于 `WanAndroidApi`：

```text
POST lg/collect/{id}/json
POST lg/uncollect_originId/{id}/json
```

页面点击收藏时，首页会先判断登录态：

```text
已登录 -> ToggleCollectUseCase(article.id, !article.collect)
未登录 -> navigate("login")
```

这个判断放在 UI 层是为了让交互更直接：未登录时立刻跳转登录页，而不是先发请求再根据 401 或业务错误处理。但真正的收藏动作仍然通过 `ToggleCollectUseCase` 进入领域用例。

收藏动作也可以继续向 Clean 架构靠拢。更完整的领域表达可以是：

```text
ToggleCollectUseCase
  -> 检查登录态
  -> 调用 CollectionRepository
  -> 更新本地 ArticleRepository 缓存
  -> 返回成功或失败
```

当前实现把“未登录跳转”放在 UI，把“调用收藏接口”放在 UseCase。这已经比 UI 直接调接口更清楚。后续如果收藏规则变复杂，可以把更多业务判断收回 UseCase。

## 当前实现的关键权衡

登录态由 Cookie 推导，而用户信息由 `GetUserInfoUseCase` 单独获取。这让“是否登录”和“用户资料展示”解耦，但也意味着 Cookie 过期时，UI 可能短时间内仍以为已登录。

收藏调用成功后，当前首页代码没有更新 Room 中的文章收藏状态。由于首页列表是 Room 驱动，接口成功本身不会自动改变 UI。更完整的链路应该是：

```text
点击收藏
  -> 乐观更新本地 ArticleEntity.collect
  -> 调用 collect/uncollect API
  -> 成功：保持本地状态
  -> 失败：回滚本地状态并提示
```

这样用户体验会更接近真实产品。

这也是 Clean 架构中“业务规则”和“交互策略”边界需要讨论的地方。未登录时跳转登录页是交互策略，适合表现层处理；“未登录不能收藏”是业务规则，可以进入 UseCase。项目越大，这类边界越需要团队统一。

## 安全和维护建议

DataStore 中保存的是 Cookie 字符串，适合示例项目。正式产品如果 Cookie 或 token 敏感性更高，可以考虑加密存储。无论换成什么存储方式，都应该限制在外层基础设施中完成。

`CookieStorage.saveCookies()` 当前按 host 存储并按 Cookie name 替换，这对玩 Android 足够。若未来支持多个域名、path、expires、secure 等更复杂规则，需要更完整地遵循 OkHttp Cookie 匹配逻辑。

登录成功后可以触发一次用户信息刷新，并把用户信息缓存下来，这样“我的”页面能更快显示，同时可以验证 Cookie 是否可用。

## 基础设施审查清单

登录和会话相关代码可以按这些问题检查：

- ViewModel 是否没有直接读写 Cookie？
- Cookie 保存是否集中在 OkHttp CookieJar 和 Storage？
- 登录状态是否通过 Repository 暴露，而不是页面自己解析 Cookie？
- Cookie 过期或失效时是否有清理策略？
- 收藏成功后是否同步本地缓存？
- 未登录跳转和未登录业务错误是否分工清楚？
- 敏感信息是否有必要加密存储？

基础设施最容易“顺手写一下”然后散落全项目。Clean 架构的价值之一，就是给这些细节一个固定归宿。
