package com.sample.wanandroidclean.feature.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.entity.ProjectChapter
import com.sample.wanandroidclean.domain.repository.UserRepository
import com.sample.wanandroidclean.domain.usecase.GetProjectArticlesUseCase
import com.sample.wanandroidclean.domain.usecase.GetProjectChaptersUseCase
import com.sample.wanandroidclean.domain.usecase.ToggleCollectUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProjectUiState(
    val isLoading: Boolean = false,
    val chapters: List<ProjectChapter> = emptyList(),
    val error: String? = null
)

class ProjectViewModel(
    private val getProjectChaptersUseCase: GetProjectChaptersUseCase,
    private val getProjectArticlesUseCase: GetProjectArticlesUseCase,
    private val toggleCollectUseCase: ToggleCollectUseCase,
    private val userRepository: UserRepository
) : ViewModel() {

    // 观察登录状态
    val isLoggedIn: StateFlow<Boolean> = userRepository.isUserLoggedIn
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    /**
     * 响应式 UI 状态流。
     * 直接观察分类列表。
     */
    val uiState: StateFlow<ProjectUiState> = getProjectChaptersUseCase()
        .map { result ->
            result.fold(
                onSuccess = { chapters ->
                    ProjectUiState(isLoading = false, chapters = chapters)
                },
                onFailure = { e ->
                    ProjectUiState(isLoading = false, error = e.localizedMessage)
                }
            )
        }
        .onStart { emit(ProjectUiState(isLoading = true)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ProjectUiState(isLoading = true)
        )

    private val pagingDataFlowMap = mutableMapOf<Int, Flow<PagingData<Article>>>()

    /**
     * 获取分页数据流。
     */
    fun getArticlesPaging(chapterId: Int): Flow<PagingData<Article>> {
        return pagingDataFlowMap.getOrPut(chapterId) {
            getProjectArticlesUseCase(chapterId)
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
