package com.sample.wanandroidclean.feature.wxarticle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.entity.WxChapter
import com.sample.wanandroidclean.domain.usecase.GetWxArticlesUseCase
import com.sample.wanandroidclean.domain.usecase.GetWxChaptersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WxArticleUiState(
    val isLoading: Boolean = false,
    val chapters: List<WxChapter> = emptyList(),
    val articles: Map<Int, List<Article>> = emptyMap(), // Keyed by chapterId
    val error: String? = null
)

class WxArticleViewModel(
    private val getWxChaptersUseCase: GetWxChaptersUseCase,
    private val getWxArticlesUseCase: GetWxArticlesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WxArticleUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadChapters()
    }

    private fun loadChapters() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getWxChaptersUseCase().fold(
                onSuccess = {
                    _uiState.update { currentState ->
                        currentState.copy(isLoading = false, chapters = it)
                    }
                    // Load articles for the first chapter
                    if (it.isNotEmpty()) {
                        loadArticlesForChapter(it.first().id)
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
            getWxArticlesUseCase(chapterId, page).onSuccess {
                _uiState.update { currentState ->
                    val newArticles = currentState.articles.toMutableMap()
                    newArticles[chapterId] = it
                    currentState.copy(articles = newArticles)
                }
            }
        }
    }
}
