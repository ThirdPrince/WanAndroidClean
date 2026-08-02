# 07. 架构演进：按 Clean 规则新增一个业务模块

这一篇把前面的 Clean 架构分析落成操作手册。假设要新增一个“搜索”模块，推荐按 Clean 的依赖方向从内到外修改：先定义领域能力，再实现数据细节，最后接入表现层和 app 装配。

## 第一步：确定领域模型和仓库接口

先在 `domain` 中定义业务需要的实体和能力。Clean 架构里，新增功能不要从页面或接口开始，而应该先问：这个业务场景需要什么模型、什么能力、什么用例？

```text
domain/entity/SearchResult.kt
domain/repository/SearchRepository.kt
domain/usecase/SearchArticlesUseCase.kt
```

原则是：只表达业务，不暴露 Retrofit DTO、Room Entity 或 Android UI 类型。

如果搜索结果需要分页，可以参考 `ArticleRepository.getArticlesPaging()`；如果只是一次性请求，可以返回 `Flow<Result<List<SearchResult>>>` 或 `suspend fun(): Result<List<SearchResult>>`。

一个可能的接口草案：

```kotlin
interface SearchRepository {
    suspend fun searchArticles(keyword: String, page: Int): Result<List<Article>>
}

class SearchArticlesUseCase(
    private val repository: SearchRepository
) {
    suspend operator fun invoke(keyword: String, page: Int): Result<List<Article>> {
        return repository.searchArticles(keyword, page)
    }
}
```

如果搜索结果本质上还是文章，可以直接复用 `Article`，不必为了“搜索结果”强行造一个新实体。Clean 架构强调业务表达，不强调文件数量。

## 第二步：在 data 层实现接口

需要改这些位置：

```text
data/remote/WanAndroidApi.kt
data/model/*
data/repository/SearchRepositoryImpl.kt
data/di/DataModule.kt
```

如果需要缓存，再加：

```text
data/local/entity/*
data/local/dao/*
data/local/AppDatabase.kt
data/datasource/*
```

简单功能可以先不建 DataSource，RepositoryImpl 直接使用 API。需要本地优先、离线缓存或多数据源组合时，再拆 DataSource。

RepositoryImpl 示例结构可以是：

```kotlin
class SearchRepositoryImpl(
    private val api: WanAndroidApi
) : SearchRepository {
    override suspend fun searchArticles(
        keyword: String,
        page: Int
    ): Result<List<Article>> {
        return safeApiCall {
            api.searchArticles(page, keyword)
        }.map { articleData ->
            articleData.datas.map { it.toDomain() }
        }
    }
}
```

注意这里返回的是 `Article`，不是 `ArticleDto`。DTO 的生命周期应该结束在 data 层。

## 第三步：注册 UseCase 和 Repository

在 `domain/di/DomainModule.kt` 注册：

```kotlin
factory { SearchArticlesUseCase(get()) }
```

在 `data/di/DataModule.kt` 绑定接口实现：

```kotlin
single<SearchRepository> { SearchRepositoryImpl(get()) }
```

这一步完成后，feature 层就可以只依赖 UseCase，而不依赖具体实现。

如果构造参数变多，不要急着把依赖从 feature 传进来。应该继续让 Koin 在模块中解析：

```text
SearchViewModel -> SearchArticlesUseCase
SearchArticlesUseCase -> SearchRepository
SearchRepository -> SearchRepositoryImpl
SearchRepositoryImpl -> WanAndroidApi
```

这条解析链越稳定，feature 越轻。

## 第四步：创建 feature 模块

如果是独立业务，建议新建：

```text
feature/search
```

模块内至少包含：

```text
SearchScreen.kt
SearchViewModel.kt
di/SearchModule.kt
build.gradle.kts
```

`build.gradle.kts` 可以参考 `feature/project` 或 `feature/system`。如果需要分页，再额外引入 Paging Compose；如果需要图片，再引入 Coil Compose。feature 模块依赖 `domain`，不直接依赖 `data`。

ViewModel 可以从一个简单状态开始：

```kotlin
data class SearchUiState(
    val keyword: String = "",
    val isLoading: Boolean = false,
    val results: List<Article> = emptyList(),
    val error: String? = null
)

class SearchViewModel(
    private val searchArticlesUseCase: SearchArticlesUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()
}
```

先把边界建对，再逐步补交互细节。不要为了赶页面，把 API、DTO、错误码直接塞进 ViewModel。

## 第五步：接入 app 导航

在 `settings.gradle.kts` 添加：

```kotlin
include(":feature:search")
```

在 `app/build.gradle.kts` 添加：

```kotlin
implementation(project(":feature:search"))
```

在 `WanAndroidApp` 加载 `searchModule`，在 `MainScreen` 注册 route。

如果搜索只是某个页面的二级入口，可以不放进底部导航，只注册一个普通 composable route。

导航接入时注意两点。

第一，route 字符串最好集中定义。当前项目已有 `NavigationItem`，新增一级页面可以延续这个模式；二级页面可以定义常量或导航扩展函数。

第二，复杂参数不要长期依赖手写字符串拼接。如果只是传文章标题和 URL，编码后放 route 可以接受；如果参数变复杂，考虑使用 `SavedStateHandle`、共享 ViewModel 或序列化参数封装。

## 第六步：保持状态模式一致

推荐 ViewModel 暴露一个稳定 UI 状态：

```kotlin
data class SearchUiState(
    val keyword: String = "",
    val isLoading: Boolean = false,
    val results: List<SearchResult> = emptyList(),
    val error: String? = null
)
```

页面事件用方法表达：

```text
onKeywordChanged(keyword)
search()
retry()
clear()
```

不要让 Composable 直接调用 Repository，也不要把 `NavController` 传进 ViewModel。

## 第七步：检查架构边界

新增功能完成后，快速自查：

- `feature/search` 是否只依赖 `domain` 和 UI 库？
- `domain` 是否没有引用 Retrofit、Room、Compose？
- DTO 到 Domain 的转换是否留在 `data` 层？
- 错误是否通过 `Result` 或状态对象表达？
- Koin 是否只在 module 中注册依赖？
- 导航字符串是否集中管理，避免到处手写？

如果这些问题答案都比较干净，新模块通常就不会破坏现有 Clean 架构。

## 新增模块常见误区

误区一：从 UI 开始写，最后再补 domain。

这样容易让接口字段和页面状态先绑定在一起。更推荐先定义 UseCase 和 Repository 接口，哪怕实现先很简单。

误区二：为了省事让 feature 依赖 data。

短期少写几个文件，长期会让 UI 和数据实现绑定。以后换缓存策略或加测试时，会付出更多成本。

误区三：每个接口都机械对应一个 UseCase。

UseCase 应该围绕业务动作，不是围绕 HTTP 接口。一个 UseCase 可以组合多个接口，一个接口也未必需要单独暴露给表现层。

误区四：过早抽象。

Clean 架构不是把所有东西都抽到极致。简单功能可以先用 RepositoryImpl 直接调用 API，不一定马上拆 RemoteDataSource、LocalDataSource。等缓存、本地优先、复用需求出现，再拆更自然。

## 一条推荐开发顺序

实际开发时，可以按这个顺序推进：

```text
1. 在 domain 定义 Entity / Repository / UseCase
2. 在 data 添加 API / DTO / RepositoryImpl / mapper
3. 在 Koin module 绑定 Repository 和 UseCase
4. 在 feature 创建 ViewModel 和 UiState
5. 写 Screen，只消费 UiState 和回调
6. 在 app 接入导航
7. 回头补错误处理、空状态、加载状态和测试
```

这个顺序不是教条，而是为了让代码从一开始就沿着 Clean 的依赖方向生长。
