# 05. 数据层：Room + Paging + Retrofit 如何实现 Repository

数据层在 Clean Architecture 中属于外层。它的职责不是定义业务规则，而是实现 `domain` 中定义的 Repository 接口。首页是这个项目里最值得细看的数据层实现，因为它使用了 Paging 3 的标准离线优先组合：`Pager` 从 Room 读分页数据，`RemoteMediator` 决定何时请求网络并写入 Room。

## 为什么首页适合离线优先

首页文章列表有几个典型特点：

- 数据是长列表，需要分页。
- 用户可能频繁刷新和滚动。
- 首屏体验重要，缓存能减少白屏。
- 网络失败时，旧数据仍然有阅读价值。

因此项目没有让 UI 直接请求 `api.getArticles(page)`，而是把 Room 放在 UI 和网络之间。对 `feature` 来说，它只是在消费 `ArticleRepository.getArticlesPaging()`。

## 核心类

首页分页涉及这些类：

```text
ArticleRepository
ArticleRepositoryImpl
HomeRemoteMediator
AppDatabase
ArticleDao
ArticleEntity
HomeRemoteKeys
GetArticlesPagingUseCase
ArticlesViewModel
ArticlesScreen
```

`ArticleRepositoryImpl.getArticlesPaging()` 创建 `Pager`，它是在外层实现内层接口：

```text
PagingConfig(pageSize = 20, prefetchDistance = 2, enablePlaceholders = false)
remoteMediator = HomeRemoteMediator(api, database)
pagingSourceFactory = { articleDao.getArticlesByCategoryId(0) }
```

这里的 `categoryId = 0` 表示首页文章。公众号和项目可以复用同一张文章表，用不同 categoryId 区分数据归属。这些都是数据层的持久化策略，不应该泄漏到领域层。

把这些类按 Clean 角色分组会更清楚：

```text
domain:
  ArticleRepository
  GetArticlesPagingUseCase
  Article

data:
  ArticleRepositoryImpl
  HomeRemoteMediator
  ArticleDao
  ArticleEntity
  HomeRemoteKeys
  WanAndroidApi

feature:
  ArticlesViewModel
  ArticlesScreen
```

同一条业务链路跨了多个模块，但每个模块只知道自己应该知道的东西。`ArticlesScreen` 不知道 `HomeRemoteKeys`，`HomeRemoteMediator` 不知道 Compose，`ArticleRepository` 不知道 Room。

## RemoteMediator 的刷新流程

`HomeRemoteMediator.load()` 根据 `LoadType` 决定页码：

- `REFRESH`：优先根据当前位置附近的 remote key 推算，否则从第 0 页开始。
- `PREPEND`：首页列表不向前加载，直接结束。
- `APPEND`：读取最后一条数据对应的 remote key，拿到下一页页码。

请求成功后进入 Room transaction：

```text
如果是 REFRESH：
  清理 remote keys
  清理首页 categoryId = 0 的旧文章

计算 prevKey / nextKey
转换置顶文章和普通文章
写入 HomeRemoteKeys
写入 ArticleEntity
```

事务的意义是保证文章和 remote key 一起成功或一起失败，避免数据库出现“有文章但没有分页 key”的半成品状态。

`RemoteMediator` 的价值在于把“什么时候请求网络”从 UI 中拿出去。没有它时，ViewModel 往往要维护：

```text
currentPage
isLoadingMore
hasMore
refreshing
appendError
```

这些状态一多，分页逻辑很容易和页面状态搅在一起。Paging 3 把分页加载状态抽象出来，`RemoteMediator` 再把网络和数据库衔接起来，ViewModel 就可以保持很薄。

在 Clean 架构里，`RemoteMediator` 是典型外层细节。它服务于 RepositoryImpl，但不会进入 domain。

## 置顶文章如何排序

首页刷新第一页时会额外请求 `getTopArticles()`。项目把置顶文章转换为 `ArticleEntity` 时设置：

```text
page = -1
orderInPage = index
isTop = true
```

普通文章则使用真实页码：

```text
page = page
orderInPage = index
isTop = false
```

Dao 查询时按 `page ASC, orderInPage ASC` 排序，所以 `page = -1` 的置顶文章会自然排在第 0 页普通文章之前。

这个设计的好处是 UI 不需要额外把置顶文章插到列表顶部。UI 收到的就是已经排序好的 `PagingData<Article>`。排序策略留在数据层，表现层只负责渲染。

代价是数据层需要约定一个特殊页码 `-1`。这类约定最好限制在 RepositoryImpl 和 Entity 范围内，不要让领域层或 UI 依赖“置顶文章 page 等于 -1”。

## 复合主键的意义

`ArticleEntity` 使用 `(id, categoryId)` 作为复合主键。这样同一篇文章可以同时出现在首页、公众号、项目或其他分类下，而不会互相覆盖。

```text
id        -> 文章本身
categoryId -> 当前列表归属
```

这比单纯用 `id` 做主键更适合多入口内容流。注意这仍然是数据层设计，领域层的 `Article` 不需要知道复合主键这回事。

## UI 如何消费 PagingData

`ArticlesViewModel` 暴露：

```kotlin
val articlesPagingData = getArticlesPagingUseCase()
    .cachedIn(viewModelScope)
```

`cachedIn(viewModelScope)` 可以让配置变化后继续复用分页流，避免 Compose 重组或 Activity 重建时重复创建分页管线。

`ArticlesScreen` 中：

```text
collectAsLazyPagingItems()
LazyColumn items(count = pagingItems.itemCount)
renderAppendState(pagingItems)
```

列表底部加载、失败重试、下拉刷新都围绕 Paging 的 `LoadState` 实现。

这里有一个重要边界：UI 可以处理 `LoadState`，因为这是 Paging 暴露给表现层的列表状态；但 UI 不应该决定下一页页码，也不应该直接调用 `api.getArticles(page)`。页码属于数据加载策略，应该留在 `RemoteMediator`。

`cachedIn(viewModelScope)` 也很关键。它让分页流绑定到 ViewModel 生命周期，避免屏幕旋转或 Compose 重组时重复触发数据源创建。这个细节虽然属于表现层代码，但它处理的是 Paging 流的生命周期，而不是数据来源细节。

## 这条链路的后续优化

收藏状态目前没有写回 `ArticleEntity`。如果要让首页收藏图标在点击后立即变化，需要给 `ArticleDao` 增加更新 collect 字段的方法，并在 `ToggleCollectUseCase` 成功后更新本地库。

`ArticleEntity` 当前没有保存 `collect` 字段，但 `ArticleItem` 会读取 `article.collect`。需要检查 `Article` 和 DTO 到 Domain 的映射是否完整，避免 UI 状态无法被本地缓存表达。

`fallbackToDestructiveMigration()` 适合开发期，正式应用应提供 Room Migration，否则升级数据库会清空用户缓存。

## 离线优先的取舍

离线优先不是所有列表都必须使用。它适合首页这种“高频访问、首屏重要、旧数据仍有价值”的场景。它的收益是：

- 首屏可以先显示缓存。
- 弱网时仍能浏览旧数据。
- PagingSource 从 Room 读取，UI 更新稳定。
- 网络刷新和数据库事务可以集中管理。

它的成本也明显：

- 需要维护 Entity、Dao、RemoteKeys。
- 需要处理数据库迁移。
- 需要考虑缓存清理策略。
- 需要同步本地状态，比如收藏字段。
- 数据一致性比纯网络请求更复杂。

所以对于体系详情文章这种变化频繁、缓存价值暂时不高的列表，项目选择直接远程请求是合理的。Clean 架构不要求所有 RepositoryImpl 都用同一种策略，它只要求这些策略不要泄漏到上层。

## 数据层审查清单

审查 RepositoryImpl 和本地缓存时，可以看这些问题：

- RepositoryImpl 是否实现 domain 接口，而不是被 feature 直接依赖？
- DTO 是否在 data 层转换成 Domain Entity？
- Room Entity 是否没有泄漏到 feature？
- RemoteMediator 的数据库写入是否使用 transaction？
- RemoteKeys 是否和列表数据一起维护？
- 刷新时是否只清理对应 category 的数据，避免误删其他列表？
- 本地缓存是否能表达 UI 需要的关键字段，比如 `collect`？
- 数据库升级是否有 Migration，而不是长期依赖 destructive migration？
