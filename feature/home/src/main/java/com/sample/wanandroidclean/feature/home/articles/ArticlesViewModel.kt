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

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    /**
     * 响应式分页数据流。
     * cachedIn 使分页状态在 ViewModel 存活期间保持，避免重复加载。
     */
    val articlesPagingData: Flow<PagingData<Article>> = getArticlesPagingUseCase()
        .cachedIn(viewModelScope)

    init {
        loadBanners()
    }

    fun loadBanners() {
        viewModelScope.launch {
            _uiState.update { it.copy(isBannersLoading = true, bannersError = null) }
            bannerRepository.getBanners().fold(
                onSuccess = { banners ->
                    _uiState.update { it.copy(isBannersLoading = false, banners = banners) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isBannersLoading = false, bannersError = e.localizedMessage) }
                }
            )
        }
    }
}
