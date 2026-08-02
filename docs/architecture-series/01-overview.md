# 01. Clean 总览：以 domain 为中心组织 Android 应用

WanAndroidClean 的架构核心是 **Clean Architecture**。多模块、Compose、Koin、Retrofit、Room、Paging 都是实现手段，真正的主线是：业务规则放在内层，技术细节放在外层，外层依赖内层，内层不反向依赖外层。

这套结构解决的是 Android 项目里最常见的失控问题：页面直接调接口、接口模型直接铺 UI、缓存逻辑散在 ViewModel、登录态到处判断、业务规则被框架代码淹没。Clean 架构把这些东西拆开，让每一层只做自己该做的事。

## Clean 架构在项目里的分层

当前项目包含这些 Gradle 模块：

```text
app
domain
data
feature/home
feature/system
feature/wxarticle
feature/project
feature/mine
feature/web
```

它们围绕 Clean Architecture 的关系可以理解为：

```text
app
 ├─ feature/home
 ├─ feature/system
 ├─ feature/wxarticle
 ├─ feature/project
 ├─ feature/mine
 ├─ feature/web
 ├─ domain
 └─ data

feature/* ──> domain
data      ──> domain
app       ──> feature/* + data + domain
```

这里最重要的是：`domain` 是内层核心。它不依赖 Android UI、不依赖 Retrofit、不依赖 Room，也不依赖具体 feature。业务核心通过实体、仓库接口和 UseCase 表达；`data` 负责实现这些接口；`feature` 只面向 `domain` 编程。

如果用 Clean Architecture 的经典圆环来对应这个项目，可以这样看：

```text
Entities / Enterprise Rules
  -> domain/entity

Use Cases / Application Rules
  -> domain/usecase
  -> domain/repository 接口

Interface Adapters
  -> feature/*/ViewModel
  -> data/repository
  -> data/model 的 DTO 转换
  -> data/local/entity 的 Entity 转换

Frameworks & Drivers
  -> Compose
  -> Retrofit / OkHttp
  -> Room
  -> DataStore
  -> Koin
  -> Navigation Compose
```

这个对应关系有助于避免一个误解：Clean 架构不是“必须有四个同名目录”，而是要把不同变化频率的代码分开。业务规则变化慢，框架和接口细节变化快；变化快的外层不应该反过来拖动变化慢的内层。

## domain 是架构中心

`domain` 是 Clean 架构的中心层。它包含 `Article`、`Banner`、`UserInfo`、`SystemCategory` 等领域实体，也包含 `ArticleRepository`、`UserRepository` 等接口，以及 `GetArticlesPagingUseCase`、`LoginUseCase`、`ToggleCollectUseCase` 等用例。

它表达的是“业务需要什么”，不是“技术如何实现”。比如首页需要文章分页能力，所以 `domain` 定义 `ArticleRepository.getArticlesPaging()`；至于这个分页来自 Retrofit、Room、RemoteMediator，还是测试 Fake，都不是 `domain` 关心的事情。

在这个项目里，`domain` 有三个明显特征：

第一，它的实体是业务语义对象。`Article` 表示文章，`Banner` 表示首页 Banner，`UserInfo` 表示用户信息。它们不是接口响应的机械搬运，也不是数据库表字段的复制。

第二，它通过 Repository 接口描述能力。比如 `UserRepository` 关心登录和登录状态，`CollectionRepository` 关心收藏和取消收藏，`SystemRepository` 关心体系分类和体系文章。这些接口描述的是“应用需要做什么”，不是“怎么请求 URL”。

第三，它通过 UseCase 给表现层提供入口。ViewModel 调用 `LoginUseCase`，读起来就是一次业务动作；调用 `GetArticlesPagingUseCase`，读起来就是一个业务查询。这个语义比直接调用某个 RepositoryImpl 更稳定。

## data 是外层适配层

`data` 负责把外部世界适配成 `domain` 需要的形状。它包含：

- `WanAndroidApi`：Retrofit 接口
- DTO：网络响应模型
- Room Entity 和 Dao：本地存储模型
- `RepositoryImpl`：实现 `domain` 中的 Repository 接口
- `RemoteMediator`：协调网络分页和本地缓存
- `CookieStorage`、`PersistentCookieJar`：登录态基础设施

这些都是 Clean 架构里的外层细节。它们可以变化，但不应该反向污染 `domain`。

`data` 层的关键价值是“翻译”。它一边面对外部世界，一边面对领域层：

```text
外部接口返回 BaseResponse<ArticleData>
  -> ArticleDto
  -> Article

Room 表中保存 ArticleEntity
  -> Article

OkHttp 收到 Set-Cookie
  -> CookieStorage 持久化
  -> UserRepository 暴露 isUserLoggedIn
```

这种翻译让外部细节被隔离起来。比如玩 Android 接口的错误码是 `errorCode`、错误文案是 `errorMsg`，这些字段不应该一路传到 UI；Room 为分页排序引入 `page`、`orderInPage`、`categoryId`，这些也不应该成为领域模型的核心概念。

## feature 是表现层

`feature/*` 是表现层。每个 feature 模块包含自己的 `Screen`、`ViewModel` 和 DI module。它依赖 `domain`，通过 UseCase 或 Repository 接口拿数据，然后把结果组织成 Compose 可消费的状态。

以首页为例：

```text
ArticlesScreen
  -> ArticlesViewModel
  -> GetArticlesPagingUseCase
  -> ArticleRepository
```

到这里为止，表现层仍然不知道数据来自网络还是数据库。

表现层的职责可以分成三块：

- Composable 描述界面结构和交互。
- ViewModel 组合页面状态，调用 UseCase，处理加载和错误。
- feature module 注册 ViewModel，让 app 层装配。

这个边界很重要。Compose 页面可以知道点击按钮后要调用 `viewModel.toggleCollect(article)`，但不应该知道收藏接口是 `POST lg/collect/{id}/json`。ViewModel 可以知道要调用 `ToggleCollectUseCase`，但不应该知道 Cookie 存在哪里。

## app 是装配层

`app` 是壳工程。它负责应用入口、主题、全局导航、Koin 初始化和跨 feature 的页面跳转。比如 `WanAndroidApp` 启动 Koin 并加载 `dataModule`、`domainModule` 以及各业务模块的 module；`MainScreen` 负责底部 Tab、文章详情页、登录页、收藏页等路由拼装。

在 Clean 架构里，`app` 可以知道所有模块，因为它处在最外层，负责组装应用。但业务规则不应该写进 `app`。

`app` 层的代码天然会比其他模块“知道更多”。它知道有哪些一级页面、哪些二级页面、哪些 Koin module 要加载，也知道主题和启动流程。但这不代表它应该成为业务大杂烩。

判断 `app` 层代码是否越界，可以看一个问题：这段逻辑如果换成另一个入口，比如平板壳工程、Wear 壳工程、测试宿主工程，还应该存在吗？如果答案是否定的，它多半是装配逻辑；如果答案是肯定的，它可能属于 `domain` 或 feature。

## 一条典型 Clean 数据链路

以首页文章列表为例：

```text
ArticlesScreen
  -> ArticlesViewModel
  -> GetArticlesPagingUseCase
  -> ArticleRepository
  -> ArticleRepositoryImpl
  -> Pager + HomeRemoteMediator
  -> WanAndroidApi + Room
  -> ArticleEntity.toDomain()
  -> PagingData<Article>
  -> Compose LazyColumn
```

这条链路里，UI 看见的是 `Article` 和 `PagingData<Article>`，不会直接接触 DTO、Room Entity 或 Retrofit Response。这就是 Clean Architecture 在这个项目里的主要价值：**外层细节可以变化，但内层业务模型和用例保持稳定**。

也可以把这条链路拆成两个方向看。

读取方向：

```text
外部数据源
  -> data 适配
  -> domain 模型
  -> ViewModel 状态
  -> Compose 渲染
```

事件方向：

```text
用户操作
  -> Composable 回调
  -> ViewModel 方法
  -> UseCase
  -> Repository 接口
  -> RepositoryImpl
  -> 外部系统
```

读取和事件都经过 `domain`，这就是 Clean 架构的稳定轴线。UI 不绕过 UseCase 去调 API，data 不把 DTO 直接丢给 UI，项目的复杂度就不会轻易扩散。

## 这个项目为什么算 Clean 架构

它不是只按目录命名的 Clean Demo，而是把核心规则落到了真实代码里：

- `domain` 定义 Repository 接口，`data` 提供 RepositoryImpl。
- `feature` 通过 UseCase 消费业务能力，而不是直接使用 Retrofit 或 Room。
- DTO、Room Entity、Cookie、RemoteMediator 都留在 `data` 这类外层模块。
- Compose 页面使用 ViewModel 暴露 `StateFlow` 或 `Flow<PagingData<T>>`，不直接承载数据来源细节。
- Koin 只负责运行时装配，不改变 Clean 架构的依赖方向。

也正因为它接近真实项目，源码里还有一些自然生长出来的权衡。例如首页收藏动作调用接口后暂未同步更新 Room 中的 `collect` 字段；导航参数仍有一部分用 URL 编码字符串手动传递；部分 feature 对 Paging 的使用还可以继续统一。这些不是架构失败，而是后续演进的抓手。

## 和普通 MVVM 的区别

如果只是 MVVM，项目也可以写成：

```text
Screen -> ViewModel -> RetrofitApi -> DTO -> UI
```

这种写法短期很快，但问题也明显。接口模型会进入 UI，ViewModel 会知道过多网络细节，缓存一加进来就容易变成 ViewModel 中的一堆 if/else。测试时也很难替换数据来源。

WanAndroidClean 在 MVVM 外面加了一层 Clean 边界：

```text
Screen -> ViewModel -> UseCase -> Repository 接口 -> RepositoryImpl -> API/DB
```

这条链路更长，但每一段都有明确职责。ViewModel 不是数据层的协调中心，RepositoryImpl 才是；API 和 DB 不是业务抽象，Repository 接口才是；Compose 不是业务规则承载者，UseCase 才是。

## 架构收益

这套 Clean 分层带来的收益主要有四个。

第一，业务和框架解耦。未来如果某个接口返回字段变化，通常只需要改 DTO 和 mapper，不需要改所有 UI。

第二，功能模块更容易独立演进。`feature/home`、`feature/mine`、`feature/system` 都依赖 `domain`，不互相依赖，减少了页面之间的隐式耦合。

第三，测试入口更清晰。UseCase 可以通过 fake Repository 测试，ViewModel 可以通过 fake UseCase 或 fake Repository 测试，不需要真的启动 Retrofit 或 Room。

第四，复杂数据策略有地方安放。比如首页的离线优先分页，不需要塞进 ViewModel，而是放在 `ArticleRepositoryImpl` 和 `HomeRemoteMediator` 中。

## 架构成本

Clean 架构不是免费的。它会引入更多文件和更多转换：

- DTO 到 Domain 需要 mapper。
- Entity 到 Domain 需要 mapper。
- Repository 接口和实现需要分开。
- 简单功能也可能多一个 UseCase。
- DI module 需要维护绑定关系。

所以判断一个项目是否适合 Clean 架构，不能只看“代码多不多”，而要看项目是否会继续增长。WanAndroidClean 有首页、体系、公众号、项目、我的、Web 详情、登录、收藏、缓存和分页，这些复杂度已经足够让 Clean 架构发挥价值。

## 本系列后续怎么读

后面的文章会沿着 Clean 架构的方向继续展开：

- 第 2 篇讲依赖规则，解释为什么 `feature` 和 `data` 都要指向 `domain`。
- 第 3 篇讲领域层，解释 Entity、Repository 接口、UseCase 分别解决什么问题。
- 第 4 篇讲表现层，解释 Compose 和 ViewModel 如何保持外层身份。
- 第 5 篇讲数据层，解释 Room、Paging、Retrofit 如何实现 Repository。
- 第 6 篇讲基础设施，解释 Cookie、登录态、收藏为什么不应该散落在页面里。
- 第 7 篇讲新增功能，给出按 Clean 规则扩展模块的步骤。
