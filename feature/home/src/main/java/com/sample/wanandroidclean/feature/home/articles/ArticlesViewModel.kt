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
    val bannersError: String? = null
)

class ArticlesViewModel(
    private val getArticlesPagingUseCase: GetArticlesPagingUseCase,
    private val bannerRepository: BannerRepository
) : ViewModel() {

    // 刷新触发器，初始发送一个信号以触发首次加载
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1).apply { tryEmit(Unit) }

    /**
     * 响应式 Banner 状态流
     * 监听 [refreshTrigger]，每当触发刷新时，重新获取数据流
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<HomeUiState> = refreshTrigger
        .flatMapLatest { 
            bannerRepository.getBanners()
                .onStart { 
                    // 这里不需要 emit 加载状态，因为 stateIn 的 initialValue 和 map 逻辑会处理
                }
        }
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
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState(isBannersLoading = true)
        )

    /**
     * 响应式分页文章数据流
     * cachedIn 确保分页状态在 ViewModel 存活期间保持
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
