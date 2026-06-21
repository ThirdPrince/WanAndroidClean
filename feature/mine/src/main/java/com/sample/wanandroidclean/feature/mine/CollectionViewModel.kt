package com.sample.wanandroidclean.feature.mine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.usecase.GetCollectionsUseCase
import com.sample.wanandroidclean.domain.usecase.ToggleCollectUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CollectionUiState(
    val isLoading: Boolean = false,
    val collections: List<Article> = emptyList(),
    val error: String? = null
)

class CollectionViewModel(
    private val getCollectionsUseCase: GetCollectionsUseCase,
    private val toggleCollectUseCase: ToggleCollectUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CollectionUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadCollections()
    }

    fun loadCollections() {
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

    /**
     * 在收藏页面切换收藏状态（通常是取消收藏）
     */
    fun toggleCollect(article: Article) {
        viewModelScope.launch {
            // 乐观更新：先从列表中移除，提升用户体验
            val oldList = _uiState.value.collections
            _uiState.update { it.copy(collections = oldList.filter { item -> item.id != article.id }) }

            toggleCollectUseCase(article.id, !article.collect).onFailure {
                // 如果失败，则回滚列表
                _uiState.update { it.copy(collections = oldList) }
            }
        }
    }
}
