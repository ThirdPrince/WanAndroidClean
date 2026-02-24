package com.sample.wanandroidclean.feature.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.entity.ProjectChapter
import com.sample.wanandroidclean.domain.usecase.GetProjectArticlesUseCase
import com.sample.wanandroidclean.domain.usecase.GetProjectChaptersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    private val _uiState = MutableStateFlow(ProjectUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadChapters()
    }

    private fun loadChapters() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getProjectChaptersUseCase().fold(
                onSuccess = { chapters ->
                    _uiState.update { currentState ->
                        currentState.copy(isLoading = false, chapters = chapters)
                    }
                    if (chapters.isNotEmpty()) {
                        loadArticlesForChapter(chapters.first().id)
                    }
                },
                onFailure = { e ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            error = e.localizedMessage ?: "An unknown error occurred"
                        )
                    }
                }
            )
        }
    }

    fun loadArticlesForChapter(chapterId: Int, page: Int = 1) {
        viewModelScope.launch {
            getProjectArticlesUseCase(page, chapterId).onSuccess { articles ->
                _uiState.update { currentState ->
                    val newArticlesMap = currentState.articles.toMutableMap()
                    newArticlesMap[chapterId] = articles
                    currentState.copy(articles = newArticlesMap)
                }
            }
        }
    }
}
