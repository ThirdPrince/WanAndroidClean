package com.sample.wanandroidclean.feature.system

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.entity.SystemCategory
import com.sample.wanandroidclean.domain.usecase.GetSystemArticlesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SystemDetailUiState(
    val articles: Map<Int, List<Article>> = emptyMap(), // Keyed by category id (cid)
    val loadingCids: Set<Int> = emptySet()
)

class SystemDetailViewModel(
    private val getSystemArticlesUseCase: GetSystemArticlesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SystemDetailUiState())
    val uiState = _uiState.asStateFlow()

    fun loadArticlesForCategory(cid: Int, page: Int = 0) {
        // If already loading or already have data, skip (for simplicity)
        if (_uiState.value.loadingCids.contains(cid) || _uiState.value.articles.containsKey(cid)) return

        viewModelScope.launch {
            _uiState.update { it.copy(loadingCids = it.loadingCids + cid) }
            
            getSystemArticlesUseCase(page, cid).onSuccess { articles ->
                _uiState.update { currentState ->
                    currentState.copy(
                        articles = currentState.articles + (cid to articles),
                        loadingCids = currentState.loadingCids - cid
                    )
                }
            }.onFailure {
                _uiState.update { it.copy(loadingCids = it.loadingCids - cid) }
            }
        }
    }
}
