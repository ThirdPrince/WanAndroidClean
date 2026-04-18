package com.sample.wanandroidclean.feature.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.entity.ProjectChapter
import com.sample.wanandroidclean.domain.usecase.GetProjectArticlesUseCase
import com.sample.wanandroidclean.domain.usecase.GetProjectChaptersUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProjectUiState(
    val isLoading: Boolean = false,
    val chapters: List<ProjectChapter> = emptyList(),
    val articles: Map<Int, List<Article>> = emptyMap(), // Keyed by chapterId
    val error: String? = null
)

class ProjectViewModel(
    private val getProjectChaptersUseCase: GetProjectChaptersUseCase,
    private val getProjectArticlesUseCase: GetProjectArticlesUseCase
) : ViewModel() {

    // 使用单独的流来管理已加载的文章，方便按需更新
    private val _articlesMap = MutableStateFlow<Map<Int, List<Article>>>(emptyMap())

    /**
     * 响应式 UI 状态流。
     * 将“分类加载逻辑”与“已加载文章映射”进行实时合并。
     */
    val uiState: StateFlow<ProjectUiState> = flow {
        // 1. 获取项目分类数据
        emit(getProjectChaptersUseCase())
    }
    .combine(_articlesMap) { chaptersResult, articlesMap ->
        // 2. 将结果转换/合并为 UI 状态
        chaptersResult.fold(
            onSuccess = { chapters ->
                ProjectUiState(
                    isLoading = false,
                    chapters = chapters,
                    articles = articlesMap
                )
            },
            onFailure = { e ->
                ProjectUiState(
                    isLoading = false,
                    error = e.localizedMessage ?: "An unknown error occurred"
                )
            }
        )
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProjectUiState(isLoading = true) // 初始显示加载中
    )

    /**
     * 由 UI (Pager) 触发，加载特定分类下的文章。
     */
    fun loadArticlesForChapter(chapterId: Int, page: Int = 1) {
        viewModelScope.launch {
            getProjectArticlesUseCase(page, chapterId).onSuccess { articles ->
                _articlesMap.update { currentMap ->
                    currentMap + (chapterId to articles)
                }
            }
        }
    }
}
