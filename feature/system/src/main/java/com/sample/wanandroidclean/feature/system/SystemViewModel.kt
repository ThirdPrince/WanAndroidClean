package com.sample.wanandroidclean.feature.system

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.wanandroidclean.domain.entity.SystemCategory
import com.sample.wanandroidclean.domain.usecase.GetSystemCategoriesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SystemUiState(
    val isLoading: Boolean = false,
    val categories: List<SystemCategory> = emptyList(),
    val error: String? = null
)

class SystemViewModel(private val getSystemCategoriesUseCase: GetSystemCategoriesUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow(SystemUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getSystemCategoriesUseCase().fold(
                onSuccess = {
                    _uiState.update { currentState ->
                        currentState.copy(isLoading = false, categories = it)
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
