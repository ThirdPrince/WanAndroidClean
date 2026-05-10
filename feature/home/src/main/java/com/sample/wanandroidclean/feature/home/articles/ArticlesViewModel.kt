package com.sample.wanandroidclean.feature.home.articles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.entity.Banner
import com.sample.wanandroidclean.domain.repository.BannerRepository
import com.sample.wanandroidclean.domain.usecase.GetArticlesPagingUseCase
import kotlinx.coroutines.flow.*

data class HomeUiState(
    val banners: List<Banner> = emptyList(),
    val isBannersLoading: Boolean = false,
    val bannersError: String? = null
)

class ArticlesViewModel(
    private val getArticlesPagingUseCase: GetArticlesPagingUseCase,
    private val bannerRepository: BannerRepository
) : ViewModel() {

    /**
     * 响应式 Banner 状态流
     * 自动从 Repository 获取“数据库缓存 + 网络刷新”的复合流
     */
    val uiState: StateFlow<HomeUiState> = bannerRepository.getBanners()
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
            // 只有当 UI 真正观察它时才会活跃，并处理配置更改（如旋屏）
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState(isBannersLoading = true)
        )

    /**
     * 响应式分页文章数据流
     * cachedIn 确保分页状态在 ViewModel 存活期间保持，避免重复加载
     */
    val articlesPagingData: Flow<PagingData<Article>> = getArticlesPagingUseCase()
        .cachedIn(viewModelScope)
}
