package com.sample.wanandroidclean.feature.home.articles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.entity.Banner
import com.sample.wanandroidclean.domain.repository.BannerRepository
import com.sample.wanandroidclean.domain.repository.UserRepository
import com.sample.wanandroidclean.domain.usecase.GetArticlesPagingUseCase
import com.sample.wanandroidclean.domain.usecase.ToggleCollectUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val banners: List<Banner> = emptyList(),
    val isBannersLoading: Boolean = false,
    val bannersError: String? = null,
    val isLoggedIn: Boolean = false // 增加登录状态追踪
)

class ArticlesViewModel(
    private val getArticlesPagingUseCase: GetArticlesPagingUseCase,
    private val bannerRepository: BannerRepository,
    private val toggleCollectUseCase: ToggleCollectUseCase,
    private val userRepository: UserRepository
) : ViewModel() {

    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1).apply { tryEmit(Unit) }

    // 观察登录状态
    val isLoggedIn: StateFlow<Boolean> = userRepository.isUserLoggedIn
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    /**
     * 响应式 Banner 状态流
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<HomeUiState> = refreshTrigger
        .flatMapLatest { 
            bannerRepository.getBanners()
                .map { result ->
                    result.fold(
                        onSuccess = { banners ->
                            HomeUiState(banners = banners, isBannersLoading = false, isLoggedIn = isLoggedIn.value)
                        },
                        onFailure = { e ->
                            HomeUiState(bannersError = e.localizedMessage, isBannersLoading = false, isLoggedIn = isLoggedIn.value)
                        }
                    )
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState(isBannersLoading = true)
        )

    /**
     * 响应式分页文章数据流
     */
    val articlesPagingData: Flow<PagingData<Article>> = getArticlesPagingUseCase()
        .cachedIn(viewModelScope)

    fun loadBanners() {
        viewModelScope.launch {
            refreshTrigger.emit(Unit)
        }
    }

    /**
     * 切换收藏状态
     */
    fun toggleCollect(article: Article) {
        viewModelScope.launch {
            // 注意：这里仅执行接口调用。
            // 由于我们使用的是 Paging 3 + Room 离线优先，
            // 完美的体验应该是：接口成功后更新本地数据库对应文章的 collect 字段，UI 会自动刷新。
            toggleCollectUseCase(article.id, !article.collect)
        }
    }
}
