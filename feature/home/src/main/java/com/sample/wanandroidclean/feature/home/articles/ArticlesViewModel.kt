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
    val bannersError: String? = null
)

class ArticlesViewModel(
    private val getArticlesPagingUseCase: GetArticlesPagingUseCase,
    private val bannerRepository: BannerRepository,
    private val toggleCollectUseCase: ToggleCollectUseCase,
    private val userRepository: UserRepository
) : ViewModel() {

    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1).apply { tryEmit(Unit) }

    // 独立的登录状态流，UI 直接观察它
    val isLoggedIn: StateFlow<Boolean> = userRepository.isUserLoggedIn
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    /**
     * 响应式 Banner 状态流
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<HomeUiState> = refreshTrigger
        .flatMapLatest { bannerRepository.getBanners() }
        .map { result ->
            result.fold(
                onSuccess = { banners -> HomeUiState(banners = banners, isBannersLoading = false) },
                onFailure = { e -> HomeUiState(bannersError = e.localizedMessage, isBannersLoading = false) }
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState(isBannersLoading = true))

    /**
     * 方案 A：纯数据库驱动的分页流。
     * 直接观察数据库，不进行内存拦截。
     */
    val articlesPagingData: Flow<PagingData<Article>> = getArticlesPagingUseCase()
        .cachedIn(viewModelScope)

    fun loadBanners() {
        viewModelScope.launch { refreshTrigger.emit(Unit) }
    }

    /**
     * 切换收藏。
     * 调用 UseCase，它会负责：更新本地 DB -> 发网络请求 -> 失败回滚 DB。
     * UI 会通过 Paging 自动感应到数据库变化并刷新。
     */
    fun toggleCollect(article: Article) {
        viewModelScope.launch {
            toggleCollectUseCase(article.id, !article.collect)
        }
    }
}
