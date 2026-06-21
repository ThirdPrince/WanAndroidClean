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
    val error: String? = null,
    val isLoggedIn: Boolean = false
)

class WxArticleViewModel(
    private val getWxChaptersUseCase: GetWxChaptersUseCase,
    private val getWxArticlesPagingUseCase: GetWxArticlesPagingUseCase,
    private val toggleCollectUseCase: ToggleCollectUseCase,
    private val userRepository: UserRepository
) : ViewModel() {

    // 独立的登录状态流
    val isLoggedIn: StateFlow<Boolean> = userRepository.isUserLoggedIn
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    /**
     * 响应式 UI 状态流
     */
    val uiState: StateFlow<WxArticleUiState> = getWxChaptersUseCase()
        .combine(isLoggedIn) { result, loggedIn ->
            result.fold(
                onSuccess = { chapters -> WxArticleUiState(chapters = chapters, isLoading = false, isLoggedIn = loggedIn) },
                onFailure = { e -> WxArticleUiState(error = e.localizedMessage, isLoading = false, isLoggedIn = loggedIn) }
            )
        }
        .onStart { emit(WxArticleUiState(isLoading = true)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WxArticleUiState(isLoading = true))

    private val pagingDataFlowMap = mutableMapOf<Int, Flow<PagingData<Article>>>()

    /**
     * 获取分页流（纯数据库驱动）
     */
    fun getArticlesPaging(chapterId: Int): Flow<PagingData<Article>> {
        return pagingDataFlowMap.getOrPut(chapterId) {
            getWxArticlesPagingUseCase(chapterId)
                .cachedIn(viewModelScope)
        }
    }

    /**
     * 切换收藏。
     * 直接由 UseCase 负责数据库乐观更新和网络同步。
     */
    fun toggleCollect(article: Article) {
        viewModelScope.launch {
            toggleCollectUseCase(article.id, !article.collect)
        }
    }
}
