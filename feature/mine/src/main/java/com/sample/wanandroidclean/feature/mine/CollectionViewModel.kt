package com.sample.wanandroidclean.feature.mine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.usecase.GetCollectionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CollectionUiState(
    val isLoading: Boolean = false,
    val collections: List<Article> = emptyList(),
    val error: String? = null
)

class CollectionViewModel(private val getCollectionsUseCase: GetCollectionsUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow(CollectionUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadCollections()
    }

    private fun loadCollections() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getCollectionsUseCase(0).fold(
                onSuccess = { list ->
                    _uiState.update { it.copy(isLoading = false, collections = list) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.localizedMessage ?: "Failed to load collections") }
                }
            )
        }
    }
}
