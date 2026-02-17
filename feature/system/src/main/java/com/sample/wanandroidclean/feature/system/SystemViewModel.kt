package com.sample.wanandroidclean.feature.system

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.wanandroidclean.domain.entity.Navigation
import com.sample.wanandroidclean.domain.entity.SystemCategory
import com.sample.wanandroidclean.domain.usecase.GetNavigationUseCase
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

data class NavigationUiState(
    val isLoading: Boolean = false,
    val navigation: List<Navigation> = emptyList(),
    val error: String? = null
)

class SystemViewModel(
    private val getSystemCategoriesUseCase: GetSystemCategoriesUseCase,
    private val getNavigationUseCase: GetNavigationUseCase
) : ViewModel() {

    private val _systemUiState = MutableStateFlow(SystemUiState())
    val systemUiState = _systemUiState.asStateFlow()

    private val _navigationUiState = MutableStateFlow(NavigationUiState())
    val navigationUiState = _navigationUiState.asStateFlow()

    init {
        loadCategories()
        loadNavigation()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _systemUiState.update { it.copy(isLoading = true, error = null) }
            getSystemCategoriesUseCase().fold(
                onSuccess = {
                    _systemUiState.update { currentState ->
                        currentState.copy(isLoading = false, categories = it)
                    }
                },
                onFailure = { e ->
                    _systemUiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            error = e.localizedMessage ?: "An unknown error occurred"
                        )
                    }
                }
            )
        }
    }

    private fun loadNavigation() {
        viewModelScope.launch {
            _navigationUiState.update { it.copy(isLoading = true, error = null) }
            getNavigationUseCase().fold(
                onSuccess = {
                    _navigationUiState.update { currentState ->
                        currentState.copy(isLoading = false, navigation = it)
                    }
                },
                onFailure = { e ->
                    _navigationUiState.update { currentState ->
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
