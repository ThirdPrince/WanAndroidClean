package com.sample.wanandroidclean.feature.wxarticle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.entity.WxChapter
import com.sample.wanandroidclean.domain.repository.UserRepository
import com.sample.wanandroidclean.domain.usecase.GetWxArticlesPagingUseCase
import com.sample.wanandroidclean.domain.usecase.GetWxChaptersUseCase
import com.sample.wanandroidclean.domain.usecase.ToggleCollectUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class WxArticleUiState(
    val isLoading: Boolean = false,
    val chapters: List<WxChapter> = emptyList(),
    val error: String? = null
)

class WxArticleViewModel(
    private val getWxChaptersUseCase: GetWxChaptersUseCase,
    private val getWxArticlesPagingUseCase: GetWxArticlesPagingUseCase,
    private val toggleCollectUseCase: ToggleCollectUseCase,
    private val userRepository: UserRepository
) : ViewModel() {

    // 观察登录状态
    val isLoggedIn: StateFlow<Boolean> = userRepository.isUserLoggedIn
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    /**
     * 响应式公众号章节状态流
     */
    val uiState: StateFlow<WxArticleUiState> = getWxChaptersUseCase()
        .map { result ->
            result.fold(
                onSuccess = { chapters ->
                    WxArticleUiState(chapters = chapters, isLoading = false)
                },
                onFailure = { e ->
                    WxArticleUiState(error = e.localizedMessage, isLoading = false)
                }
            )
        }
        .onStart { emit(WxArticleUiState(isLoading = true)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = WxArticleUiState(isLoading = true)
        )

    private val pagingDataFlowMap = mutableMapOf<Int, Flow<PagingData<Article>>>()

    fun getArticlesPaging(chapterId: Int): Flow<PagingData<Article>> {
        return pagingDataFlowMap.getOrPut(chapterId) {
            getWxArticlesPagingUseCase(chapterId)
                .cachedIn(viewModelScope)
        }
    }

    /**
     * 切换收藏状态
     */
    fun toggleCollect(article: Article) {
        viewModelScope.launch {
            toggleCollectUseCase(article.id, !article.collect)
        }
    }
}
