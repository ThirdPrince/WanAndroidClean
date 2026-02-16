package com.sample.wanandroidclean.feature.home.articles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.entity.Banner
import com.sample.wanandroidclean.domain.usecase.GetHomeScreenDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val banners: List<Banner> = emptyList(),
    val articles: List<Article> = emptyList(),
    val error: String? = null
)

class ArticlesViewModel(
    private val getHomeScreenDataUseCase: GetHomeScreenDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            getHomeScreenDataUseCase().fold(
                onSuccess = { data ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            banners = data.banners ?: emptyList(),
                            articles = data.articles ?: emptyList(),
                            error = if (data.banners == null || data.articles == null) "Failed to load some data" else null
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.localizedMessage ?: "An unknown error occurred"
                        )
                    }
                }
            )
        }
    }
}
