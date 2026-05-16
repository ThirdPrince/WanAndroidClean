package com.sample.wanandroidclean.feature.home.articles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.entity.Banner
import com.sample.wanandroidclean.domain.repository.BannerRepository
import com.sample.wanandroidclean.domain.usecase.GetArticlesPagingUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val banners: List<Banner> = emptyList(),
    val isBannersLoading: Boolean = false,
    val bannersError: String? = null,
    val isRefreshing: Boolean = false
)

class ArticlesViewModel(
    private val getArticlesPagingUseCase: GetArticlesPagingUseCase,
    private val bannerRepository: BannerRepository
) : ViewModel() {

    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1).apply { tryEmit(Unit) }

    /**
     * 响应式 Banner 状态流
     * 移除内部 .onStart，让离线数据在刷新时得以保留，实现“静默刷新”体验
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<HomeUiState> = refreshTrigger
        .flatMapLatest { 
            bannerRepository.getBanners()
                .map { result ->
                    result.fold(
                        onSuccess = { banners ->
                            HomeUiState(banners = banners, isBannersLoading = false)
                        },
                        onFailure = { e ->
                            HomeUiState(bannersError = e.localizedMessage, isBannersLoading = false)
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

    /**
     * 供 UI 调用的刷新方法
     */
    fun loadBanners() {
        viewModelScope.launch {
            refreshTrigger.emit(Unit)
        }
    }
}
