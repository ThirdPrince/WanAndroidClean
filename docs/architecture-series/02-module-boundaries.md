# 02. 依赖规则：模块边界、依赖反转与 Koin 组装

Clean Architecture 最核心的规则是依赖规则：**源码依赖只能从外层指向内层，不能从内层指向外层**。WanAndroidClean 的多模块设计就是为这条规则服务的。

多模块项目最怕“目录看起来分层，依赖实际乱飞”。这个项目的边界整体比较清晰：`domain` 定义业务规则，`data` 实现这些规则需要的外部细节，`feature` 消费业务规则，`app` 负责把所有东西装起来。

## Gradle 模块关系

`settings.gradle.kts` 中注册了这些模块：

```kotlin
include(":app")
include(":domain")
include(":data")
include(":feature:home")
include(":feature:system")
include(":feature:wxarticle")
include(":feature:project")
include(":feature:mine")
include(":feature:web")
```

`domain` 是 Kotlin JVM 模块，尽量保持纯净。它引入了 Paging Common、Coroutines 和 Koin Core，其中 Paging Common 是为了让仓库接口能够暴露 `PagingData` 类型。这是 Android 实战里的一个折中：领域层接受分页抽象，但不接受 Retrofit、Room、Compose 这些外层实现。

`data` 是 Android Library 模块，依赖 `domain`，并引入 Room、Retrofit、OkHttp、DataStore、Koin Android、Paging Runtime 等数据层工具。

多数 `feature/*` 是 Android Library + Compose 模块，依赖 `domain`，不直接依赖 `data`。这样页面层不会直接 new Retrofit，也不会直接操作 Room。

`app` 是 Application 模块，依赖所有 feature、`domain` 和 `data`。这让它可以在应用入口处完成 DI 组装和全局导航拼接。

## 依赖方向

理想依赖方向如下：

```text
feature/* -> domain <- data
app -> feature/*
app -> data
app -> domain
```

这表示业务 UI 和数据实现都指向领域抽象。`domain` 不知道数据来自网络、数据库、内存，还是测试 fake。这就是依赖反转：不是 UI 依赖具体数据实现，而是 `data` 去实现 `domain` 规定的接口。

例如 `domain.repository.ArticleRepository` 定义“首页文章分页”这个能力，`data.repository.ArticleRepositoryImpl` 负责用 `Pager`、`HomeRemoteMediator` 和 Room 实现它。`feature.home.ArticlesViewModel` 只拿 `GetArticlesPagingUseCase`，不关心背后的实现。

可以把依赖方向再具体到源码 import 层面：

```text
feature/home 可以 import:
  com.sample.wanandroidclean.domain.*
  androidx.compose.*
  androidx.lifecycle.*
  org.koin.androidx.compose.*

feature/home 不应该 import:
  com.sample.wanandroidclean.data.remote.WanAndroidApi
  com.sample.wanandroidclean.data.local.AppDatabase
  com.sample.wanandroidclean.data.repository.ArticleRepositoryImpl
```

这个规则很朴素，但非常有效。只要 feature 不 import data，表现层就不会被 Retrofit、Room、OkHttp 绑住。只要 domain 不 import Android UI 或 data，业务抽象就不会被外层细节绑住。

## 为什么不是 feature -> data -> domain

很多 Android 项目会自然写成：

```text
feature -> data -> domain
```

看起来也有三层，但这不是 Clean Architecture 的重点。问题在于，如果 feature 直接依赖 data，那么 feature 拿到的往往是具体实现：某个 Retrofit service、某个 Room dao、某个 repository impl。表现层会逐渐知道“数据怎么来”，而不是只知道“业务能做什么”。

WanAndroidClean 的方向是：

```text
feature -> domain
data -> domain
```

`feature` 和 `data` 不直接互相依赖，它们通过 `domain` 连接。`domain` 就像一份业务契约，表现层按契约使用，数据层按契约实现。

## Repository 是依赖反转的关键点

Clean 架构里最容易讲虚的地方，就是“抽象”。在这个项目中，抽象最清楚地落在 Repository 接口上。

```text
domain/repository/ArticleRepository.kt
data/repository/ArticleRepositoryImpl.kt
```

接口在内层，实现外层。这样 `domain` 可以规定业务需要什么能力，`data` 再决定如何用 API、Room、Cookie、缓存策略去完成它。

如果反过来让 `feature` 直接依赖 `ArticleRepositoryImpl`，那么 UI 就会被数据实现细节绑住。后续要换缓存方案、给测试注入 fake、拆数据源，都会牵动表现层。

依赖反转还有一个测试层面的好处。假设要测试 `ArticlesViewModel`，理论上可以提供一个假的 `GetArticlesPagingUseCase` 或假的 `ArticleRepository`，让它返回固定数据。测试不需要真实网络，不需要真实数据库，也不需要知道 `HomeRemoteMediator` 的存在。

接口在内层，fake 也可以面向内层接口编写：

```kotlin
class FakeArticleRepository : ArticleRepository {
    override fun getArticlesPaging(): Flow<PagingData<Article>> {
        return flowOf(PagingData.from(fakeArticles))
    }
}
```

这就是 Clean 架构里“可替换细节”的实际意义。

## 模块职责边界

可以用下面这张表检查职责是否放对：

| 模块 | 应该放什么 | 不应该放什么 |
| :--- | :--- | :--- |
| `domain` | 领域实体、Repository 接口、UseCase | Retrofit、Room、Compose、Android Context |
| `data` | API、DTO、Entity、Dao、RepositoryImpl、缓存策略 | Compose 页面、导航逻辑 |
| `feature/*` | Screen、ViewModel、UiState、页面交互 | Retrofit API、Room Dao、RepositoryImpl |
| `app` | Application、主题、导航、DI 总装配 | 具体业务规则、接口请求细节 |

这张表不是为了限制写法，而是为了让变化留在正确的位置。接口变了，优先改 `data`；页面交互变了，优先改 `feature`；业务动作变了，优先看 `domain/usecase`。

## Koin 只负责装配，不负责分层

Koin 在这个项目里不是架构核心，它只是把 Clean 架构中的抽象和实现连接起来。项目的 DI 分三层：

```text
dataModule   -> 注册数据库、Dao、API、OkHttp、Cookie、RepositoryImpl
domainModule -> 注册 UseCase
featureModule -> 注册 ViewModel
```

应用启动时，`WanAndroidApp` 调用：

```kotlin
startKoin {
    androidContext(this@WanAndroidApp)
    modules(
        dataModule,
        domainModule,
        homeModule,
        systemModule,
        wxArticleModule,
        projectModule,
        mineModule
    )
}
```

这个位置有一个重要含义：feature 模块不需要知道 RepositoryImpl 是什么，只要 Koin 容器里已经有 `ArticleRepository` 的实现，ViewModel 所需的 UseCase 就能被注入。换成 Hilt 或手写依赖注入，Clean 分层原则也不应该变化。

从依赖解析的角度看，Koin 做了这些事：

```text
ArticlesViewModel 需要 GetArticlesPagingUseCase
GetArticlesPagingUseCase 需要 ArticleRepository
ArticleRepository 绑定到 ArticleRepositoryImpl
ArticleRepositoryImpl 需要 WanAndroidApi 和 AppDatabase
WanAndroidApi 来自 Retrofit
AppDatabase 来自 Room
```

ViewModel 只声明自己需要 UseCase。至于 UseCase 背后怎么一路解析到 Retrofit 和 Room，是 DI 容器负责的。这样业务代码不用手动拼装对象，也不需要知道每个实现类的构造细节。

需要注意的是，DI 容器也可能被滥用。不要因为 Koin 可以在任何地方 `get()`，就让 UI 页面直接获取 API 或 Dao。依赖注入解决的是对象创建问题，不是架构边界问题。

## app 层的特殊角色

`app` 不应该承载具体业务逻辑，但它需要知道所有页面，因为它负责跨模块导航。当前 `MainScreen` 做了几件事：

- 创建 `NavController`
- 判断当前 route 是否显示底部导航
- 注册首页、体系、公众号、项目、我的等一级页面
- 注册登录、收藏、WebView、体系详情等二级页面
- 处理文章标题和 URL 的编码传参

这是一种常见的中心化导航方式，优点是直观，缺点是 `app` 会 import 每个 feature 的 Screen。随着页面增多，可以考虑让每个 feature 提供 route 注册函数，`app` 只负责组合。

更进一步的演进方向可以是：

```text
feature/home 提供 homeGraph(...)
feature/mine 提供 mineGraph(...)
feature/system 提供 systemGraph(...)
app/MainScreen 只调用这些 graph 注册函数
```

这样 `app` 仍然负责总装配，但每个 feature 的内部路由可以留在自己的模块中。当前项目规模还不算大，中心化 `MainScreen` 是可以接受的；当页面继续增加时，再抽导航 graph 会更自然。

## 当前边界里的几个细节

`feature/web` 没有依赖 `domain`，因为它只负责展示 WebView，不需要业务实体。这是合理的轻量模块。

`domain` 暴露了 Paging Common 类型，这让分页能力进入领域接口。严格 Clean Architecture 会避免 framework 类型进入 domain，但 Android 实战里这通常是可接受的折中：换来 feature 层使用 Paging Compose 的简洁性。

`data` 中 `DataModule.kt` 目前同时负责基础设施、数据库、网络、仓库绑定、图片加载器注册。它清楚但偏大。后续可以拆成 `networkModule`、`databaseModule`、`repositoryModule`、`imageModule`，降低单文件认知成本。

## 边界检查清单

日常开发时，可以用这几个问题判断有没有破坏 Clean 架构：

- `domain` 里是否出现了 `android.*`、`retrofit2.*`、`androidx.room.*`、`androidx.compose.*`？
- `feature/*` 里是否直接 import 了 `data.remote`、`data.local`、`data.repository.*Impl`？
- ViewModel 是否直接调用了 Retrofit API 或 Dao？
- RepositoryImpl 是否把 DTO 或 Entity 原样返回给了 feature？
- 新增业务动作是否有对应 UseCase，还是直接塞进了 ViewModel？
- Koin module 是否出现了跨层乱拿依赖的情况？

这些问题不需要每次都形式化检查，但当项目变大、多人协作时，它们能快速发现架构边界正在松动。
