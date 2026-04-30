package com.sample.wanandroidclean.feature.system

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.wanandroidclean.domain.entity.Navigation
import com.sample.wanandroidclean.domain.entity.SystemCategory
import com.sample.wanandroidclean.domain.usecase.GetNavigationUseCase
import com.sample.wanandroidclean.domain.usecase.GetSystemCategoriesUseCase
import kotlinx.coroutines.flow.*

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

    /**
     * 响应式体系分类状态流
     */
    val systemUiState: StateFlow<SystemUiState> = flow {
        emit(SystemUiState(isLoading = true))
        getSystemCategoriesUseCase().collect { result ->
            val categories = result.getOrNull()
            if (categories != null) {
                emit(SystemUiState(categories = categories))
            } else {
                emit(SystemUiState(error = result.exceptionOrNull()?.localizedMessage))
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SystemUiState(isLoading = true)
    )

    /**
     * 响应式导航数据状态流
     */
    val navigationUiState: StateFlow<NavigationUiState> = flow {
        emit(NavigationUiState(isLoading = true))
        getNavigationUseCase().fold(
            onSuccess = { emit(NavigationUiState(navigation = it)) },
            onFailure = { e -> emit(NavigationUiState(error = e.localizedMessage)) }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NavigationUiState(isLoading = true)
    )
}
