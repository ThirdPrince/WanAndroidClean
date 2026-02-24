package com.sample.wanandroidclean.feature.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.entity.ProjectChapter
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
    private val getProjectChaptersUseCase: GetProjectChaptersUseCase
    // We will need a use case to get articles for a project chapter later
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
                onSuccess = {
                    _uiState.update { currentState ->
                        currentState.copy(isLoading = false, chapters = it)
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
}
