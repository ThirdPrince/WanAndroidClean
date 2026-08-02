# 04. 表现层：Compose + ViewModel 如何遵守 Clean 边界

在 Clean Architecture 中，表现层属于外层。它可以依赖 `domain`，但不应该依赖 `data` 的实现细节。WanAndroidClean 的表现层采用 Compose + ViewModel + Flow，核心原则是：页面只负责描述界面，ViewModel 负责持有状态和触发用例，数据来源细节留在内层接口背后的实现中。

## UI 层结构

每个 feature 基本遵循这个模式：

```text
FeatureScreen
FeatureViewModel
FeatureModule
```

例如首页：

```text
ArticlesScreen
ArticlesViewModel
HomeModule
```

`ArticlesScreen` 通过 `koinViewModel()` 获取 ViewModel，通过 `collectAsStateWithLifecycle()` 收集状态，并把 `PagingData` 转成 `LazyPagingItems`。它不会直接调用 Retrofit，也不会直接读 Room。

从 Clean 架构角度看，表现层有两个边界：

```text
Composable 边界：
  只描述 UI、收集状态、发出用户事件

ViewModel 边界：
  调用 UseCase、组合 UiState、暴露 Flow/StateFlow
```

这两个边界都不能越过 `domain` 去碰 `data`。如果 Composable 里出现 `WanAndroidApi`，说明 UI 直接依赖了外部接口；如果 ViewModel 里出现 `ArticleDao`，说明缓存策略跑到了表现层。

## Screen 的职责

Composable 应该尽量保持声明式。以首页为例，`ArticlesScreen` 做的是：

- 收集 `uiState`
- 收集 `articlesPagingData`
- 根据 `LoadState` 展示刷新、错误、底部加载
- 响应文章点击、Banner 点击、收藏点击
- 把未登录时的跳转交给 `onLoginClick`

它不应该做：

- 拼接接口地址
- 判断业务错误码
- 直接更新数据库
- 手动保存 Cookie
- 决定分页下一页页码

这让 Composable 的代码可以围绕“界面如何呈现”组织，而不是变成业务流程脚本。

## ViewModel 的职责

ViewModel 是表现层的状态协调者。它知道页面需要哪些 UseCase，也知道页面状态如何组合，但它不应该知道数据层细节。

首页 ViewModel 的职责可以这样拆：

```text
GetArticlesPagingUseCase -> articlesPagingData
BannerRepository/GetBanners -> HomeUiState.banners
UserRepository.isUserLoggedIn -> 登录状态
ToggleCollectUseCase -> 收藏动作
```

它做的是“把领域能力组织成页面状态”。这和 RepositoryImpl 的职责不同：RepositoryImpl 负责“把外部数据组织成领域能力”。

## StateFlow 承载页面状态

首页定义了 `HomeUiState`：

```kotlin
data class HomeUiState(
    val banners: List<Banner> = emptyList(),
    val isBannersLoading: Boolean = false,
    val bannersError: String? = null,
    val isLoggedIn: Boolean = false
)
```

这个状态负责 Banner、加载态、错误和登录态。文章列表本身没有塞进 `HomeUiState`，而是单独暴露：

```kotlin
val articlesPagingData: Flow<PagingData<Article>>
```

这是一个合理拆分：分页列表有自己的生命周期和加载状态，交给 Paging 管；普通页面状态交给 `StateFlow`。从 Clean 角度看，UI 收到的是领域模型 `Article` 和 `Banner`，不是 DTO 或数据库 Entity。

一个页面是否应该只有一个 `UiState`，没有绝对答案。对普通列表来说，一个 `UiState` 足够；对 Paging 列表来说，`PagingData` 自带加载状态和分页生命周期，强行塞进一个大状态反而会复杂。

当前首页的拆分比较实际：

```text
HomeUiState
  -> Banner、Banner 加载态、Banner 错误、登录态

Flow<PagingData<Article>>
  -> 文章分页列表
```

这说明 Clean 架构不要求所有东西都包装成同一种形式。它要求的是：表现层消费领域模型，数据细节不要泄漏。

## 单向数据流

以首页收藏按钮为例，数据流是：

```text
用户点击收藏按钮
  -> ArticlesScreen 判断是否登录
  -> 已登录：viewModel.toggleCollect(article)
  -> 未登录：onLoginClick()
  -> MainScreen navigate("login")
```

UI 负责把用户事件转成 ViewModel 方法或导航回调。ViewModel 不直接拿 `NavController`，因此业务状态和导航实现保持分离。ViewModel 调用的是 UseCase，而不是外层数据实现。

更完整地看，单向数据流是：

```text
State:
  UseCase/Repository Flow -> ViewModel -> UiState -> Composable

Event:
  Composable -> ViewModel -> UseCase -> Repository -> data

Navigation:
  Composable -> 回调 -> app/MainScreen -> NavController
```

把 Navigation 单独列出来很重要。导航通常是表现层或 app 装配层的事，不是领域层的事。`LoginUseCase` 不应该知道登录成功后跳到哪个页面，`ArticleRepository` 也不应该知道点击文章后打开 WebView。

## Compose 页面如何处理加载与错误

首页文章列表使用 Paging 的 `LoadState`：

- `refresh is LoadState.Loading` 时参与下拉刷新状态
- `refresh is LoadState.Error` 时展示 snackbar
- `append is LoadState.Loading` 时在列表底部展示进度
- `append is LoadState.Error` 时展示重试入口

Banner 则通过 `HomeUiState` 控制加载和错误。这说明同一个页面可以有多个状态来源，但 UI 层需要把它们组合成用户可理解的体验。

错误处理也要遵守层次边界。数据层可以把网络错误、HTTP 错误、业务错误包装成 `Result.failure`；ViewModel 可以把错误转换成页面状态；Composable 决定用 snackbar、错误页、toast 还是重试按钮展示。

换句话说：

```text
data 层：错误是什么
domain/usecase：业务动作是否成功
ViewModel：页面进入什么状态
Composable：状态如何展示
```

如果 RepositoryImpl 直接决定“弹 snackbar”，它就越过了表现层；如果 Composable 解析 HTTP code，它就越过了数据层。

## MainScreen 是跨模块 UI 编排层

`MainScreen` 不是具体业务页面，但它决定整体交互结构：

- 底部导航是否显示
- 一级页面和二级页面的路由
- 页面切换动画
- 文章详情页参数编码和解码
- 从体系页面进入体系详情时临时保存选中的分类

这类代码放在 `app` 层是合适的，因为它天然跨 feature。后续如果 route 继续变多，可以把 route 常量、参数编码、导航扩展函数抽出来，让 `MainScreen` 更薄。无论怎么抽，导航仍然应该是外层细节，不应该进入 `domain`。

## 当前 UI 状态的可优化点

首页 `HomeUiState.isLoggedIn` 是在 Banner 请求结果里读取 `isLoggedIn.value` 填进去的，而 ViewModel 又单独暴露了 `isLoggedIn: StateFlow<Boolean>`。这可能导致登录态和 `uiState` 内字段存在不同步窗口。更稳的方式是用 `combine(refreshTriggerBannerFlow, isLoggedIn)` 组合出最终 UI 状态。

收藏动作调用接口后没有更新本地 Room 中的 `ArticleEntity.collect`。在离线优先列表里，UI 数据来自数据库，接口成功后最好同步更新本地字段，或先乐观更新再失败回滚。

部分页面可能会重复处理文章点击、URL 编码、WebView 跳转。可以抽一个统一的 `navigateToArticleDetail(article)` 扩展，减少路由字符串散落。

## 表现层代码审查清单

审查 feature 代码时，可以重点看这些问题：

- Composable 是否只依赖 ViewModel、UiState、回调和领域模型？
- ViewModel 是否只依赖 UseCase 或 domain 层接口？
- 页面状态是否能完整表达 loading、content、empty、error？
- 导航是否通过回调交给 app 或上层，而不是塞进 UseCase？
- 错误文案是否在表现层统一处理，而不是从底层一路透传到 UI？
- `collectAsStateWithLifecycle()` 是否用于生命周期感知收集？
- Paging 的 `refresh`、`append`、`retry`、`refresh()` 是否各自处理清楚？

这些问题看起来细，但它们决定了表现层会不会慢慢吞掉业务层和数据层职责。
