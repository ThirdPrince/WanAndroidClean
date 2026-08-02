# 03. 领域层：Entity、Repository 接口与 UseCase

一个 Clean Architecture 项目是否真的清楚，关键要看 `domain` 是否稳定、独立、能表达业务。WanAndroidClean 的领域层主要由三类对象组成：Domain Entity、Repository 接口、UseCase。

远端 API、DTO、Room Entity、Cookie、网络异常处理都不是领域层的核心，它们属于外层细节。领域层关心的是：应用有什么业务对象、需要哪些业务能力、这些能力以什么用例提供给表现层。

## Domain Entity

Domain Entity 位于 `domain/entity`，是 UI、UseCase、Repository 接口共同使用的业务模型，例如：

```text
Article
Banner
UserInfo
SystemCategory
Navigation
WxChapter
ProjectChapter
```

这些对象应该尽量表达业务含义，而不是表达接口字段或数据库表字段。比如 `Article` 是文章这个业务概念，`ArticleDto` 是接口返回结构，`ArticleEntity` 是数据库表结构，它们不应该混成一个对象。

以 `Article` 为例，领域模型关注的是页面和业务真正需要的信息：

```kotlin
data class Article(
    val id: Int,
    val title: String,
    val author: String,
    val shareUser: String,
    val link: String,
    val isTop: Boolean = false,
    val collect: Boolean = false
)
```

这里没有 `errorCode`、`pageCount`、`curPage`，也没有 Room 需要的 `categoryId`、`page`、`orderInPage`。这些字段不是文章业务概念本身，而是接口包装或本地存储策略。

领域模型要避免两个极端。一个极端是完全照搬接口字段，导致服务端结构污染全项目；另一个极端是过度抽象，把简单数据也包装成复杂继承层次。当前项目的领域模型比较务实：字段少、语义明确、适合 Compose 页面直接使用。

## Repository 接口

Repository 接口位于 `domain/repository`，它们定义业务需要什么数据能力，例如：

```text
ArticleRepository
BannerRepository
UserRepository
CollectionRepository
SystemRepository
WxArticleRepository
ProjectRepository
```

这里的重点是“接口在 domain，实现放 data”。例如首页只需要知道有一个 `ArticleRepository.getArticlesPaging()`，不需要知道它背后是 Retrofit 请求、Room 查询，还是 Paging RemoteMediator。

Repository 接口不是简单的“API 包装器”。它应该表达应用视角的数据能力，而不是一比一复制后端接口。

比如首页接口有：

```text
GET article/list/{page}/json
GET article/top/json
```

但领域层没有暴露两个零散方法让 ViewModel 自己拼，而是提供 `getArticlesPaging()`。置顶文章和普通文章如何合并、如何缓存、如何分页，是数据层的事情。

这也是 Repository 和 ApiService 的区别：ApiService 描述 HTTP，Repository 描述业务数据能力。

## 为什么 Repository 接口放在 domain

这是 Clean 架构里最关键的依赖反转点。

如果 `feature` 直接依赖 `data.repository.ArticleRepositoryImpl`，UI 层就会知道数据来自 Retrofit、Room、RemoteMediator。这样一来，换缓存策略、换接口实现、做 fake 测试都会牵动 UI。

当前项目的做法是：

```text
domain 定义 ArticleRepository
data 实现 ArticleRepositoryImpl
feature 通过 UseCase 使用 ArticleRepository
```

这样 `domain` 拥有抽象，`data` 依赖抽象并实现它。业务规则不依赖数据细节，数据细节反过来服务业务规则。

## UseCase

UseCase 位于 `domain/usecase`，它们把 Repository 能力包装成更贴近页面和业务动作的入口，例如：

```text
GetArticlesPagingUseCase
GetBannersUseCase
LoginUseCase
ToggleCollectUseCase
GetSystemCategoriesUseCase
GetWxArticlesPagingUseCase
```

UseCase 在小项目里容易显得“多一层”，但它是 Clean 架构表达业务动作的地方。

`LoginUseCase` 表达“登录”这个动作；`ToggleCollectUseCase` 表达“切换收藏”这个动作；`GetArticlesPagingUseCase` 表达“获取首页分页文章”这个读取场景。ViewModel 不需要知道背后 Repository 如何组合 API、Room 或 Cookie。

UseCase 带来两个好处。

第一，它给 ViewModel 一个稳定、语义化的入口。`LoginViewModel` 调用 `LoginUseCase(username, password)`，而不是直接关心登录接口。

第二，它为未来组合业务留位置。比如“收藏文章”现在只是调用接口，未来可以在 `ToggleCollectUseCase` 中加入登录检查、本地乐观更新、失败回滚、埋点等，不需要把这些逻辑塞进 Compose 页面。

UseCase 的粒度不宜过大，也不宜过小。

太大的 UseCase 会变成“万能业务管理器”，里面塞满页面逻辑；太小的 UseCase 只是机械转发，读起来没有业务意义。当前项目的命名基本按用户动作或页面数据需求划分：

```text
LoginUseCase                  -> 用户登录
ToggleCollectUseCase          -> 切换收藏
GetBannersUseCase             -> 获取首页 Banner
GetSystemCategoriesUseCase    -> 获取体系分类
GetArticlesPagingUseCase      -> 获取首页分页文章
```

如果未来出现“登录后刷新用户信息并同步收藏状态”这样的组合动作，就可以新增一个更高层的 UseCase，而不是把组合逻辑散进多个 ViewModel。

## DTO 和 Entity 为什么不进 domain

接口返回的数据结构经常包含服务端细节，比如字段命名、分页包装、错误码、嵌套结构。数据库表结构又会包含主键、索引、分页排序、RemoteKeys 等持久化细节。它们都不是业务本身。

项目通过类似 `dto.toDomain()`、`entity.toDomain()` 的转换，把外层模型压缩成业务真正需要的模型。比如文章最终在领域层关心：

```text
id
title
author
shareUser
link
collect
isTop
```

这样未来接口字段变化或表结构变化时，大多数改动会留在 `data` 层。

当前项目里至少有三种模型转换：

```text
ArticleDto.toDomain()
ArticleEntity.toDomain()
ArticleEntity.fromDomain(...)
```

这些转换虽然增加了代码量，但换来的是边界清晰。DTO 负责适配服务端，Entity 负责适配数据库，Domain Entity 负责表达业务。三者可以相似，但不应该默认合并。

一个典型例子是收藏列表。接口中收藏文章可能使用 `originId` 表示原始文章 id，而普通文章使用 `id`。`ArticleDto.toDomain()` 会把它们统一成领域层的 `id`。如果 UI 直接使用 DTO，每个页面都要知道 `originId` 的特殊含义。

## 外层数据如何进入领域层

一条普通请求通常长这样：

```text
RepositoryImpl
  -> safeApiCall { api.someRequest() }
  -> Result<Dto>
  -> map { dto.toDomain() }
  -> Result<DomainEntity>
```

这里 `safeApiCall`、`api.someRequest()`、DTO 都在 `data` 层；`DomainEntity` 和 Repository 接口属于 `domain`；ViewModel 最终只看到领域模型。

`safeApiCall` 的位置也很关键。它处理的是玩 Android 接口协议和网络异常：

```text
HTTP / IOException
业务 errorCode / errorMsg
data 为空
```

这些都属于数据层和外部服务交互的细节。领域层可以接收 `Result<T>` 这种结果表达，但不需要知道错误码来自哪个字段，也不需要知道 Retrofit 抛了什么异常。

## RepositoryImpl 的几种实现策略

项目里不止一种数据策略。

首页文章使用 `Pager + RemoteMediator + Room`。它适合长列表：UI 从数据库分页读取，RemoteMediator 在需要时拉网络并写库。

体系分类使用本地优先策略：先读 Room，有缓存就先 emit；然后请求远程，成功后刷新本地并再次 emit。这适合不频繁变化、体量不大的分类树。

体系文章列表目前直接请求远程。这适合数据变化频繁、并且暂时不需要离线体验的列表。

登录和收藏直接走接口。登录成功后的 Cookie 由 OkHttp CookieJar 自动保存，不需要 LoginViewModel 手动处理 Cookie。

这些策略说明 RepositoryImpl 不只是“把接口结果返回出去”，而是外层策略的承载点。它可以决定：

- 是否先读缓存再请求网络。
- 是否把网络结果写入 Room。
- 是否合并多个接口结果。
- 是否对错误降级。
- 是否把 DTO 转成树形领域模型。
- 是否通过 Paging 暴露长列表。

只要 RepositoryImpl 最终遵守 Repository 接口，内部策略就可以演进。比如首页以后从“Room + RemoteMediator”换成“网络优先 + 内存缓存”，理论上不应该影响 `ArticlesViewModel`。

## 领域层的测试思路

Clean 架构的一个收益是领域层更容易测试。以 `ToggleCollectUseCase` 为例，它只依赖 `CollectionRepository` 接口。测试时可以提供 fake：

```kotlin
class FakeCollectionRepository : CollectionRepository {
    var collectedId: Int? = null
    var uncollectedId: Int? = null

    override suspend fun collect(id: Int): Result<Unit> {
        collectedId = id
        return Result.success(Unit)
    }

    override suspend fun uncollect(id: Int): Result<Unit> {
        uncollectedId = id
        return Result.success(Unit)
    }
}
```

这样可以验证 `ToggleCollectUseCase(id, true)` 调用 collect，`ToggleCollectUseCase(id, false)` 调用 uncollect。测试不需要启动 Android，不需要联网，也不需要 Room。

## 领域层演进建议

当前项目的领域层已经具备 Clean 架构骨架，后续可以继续增强几个方向：

- 为复杂 UseCase 添加单元测试，尤其是登录、收藏、分页入口。
- 对错误类型做更明确的领域表达，比如封装业务错误、网络错误、未登录错误。
- 让收藏状态更新进入领域用例，而不是只在 ViewModel 里调用接口。
- 检查哪些 Repository 方法是页面导向的，哪些是业务导向的，避免接口越来越散。
- 如果 Paging 类型污染领域层的争议变大，可以定义领域自己的分页抽象，或接受当前 Android 实战折中。

## 小结

Clean 架构下的 `domain` 不应该是空壳。它至少要稳定表达三件事：

- 业务对象是什么
- 业务需要哪些能力
- 这些能力通过哪些用例暴露给表现层

只要这三件事稳定，外层的数据来源、缓存策略和 UI 实现就可以相对自由地演进。
