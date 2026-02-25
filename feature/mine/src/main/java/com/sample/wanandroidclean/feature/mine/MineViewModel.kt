package com.sample.wanandroidclean.feature.mine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.wanandroidclean.domain.entity.UserInfo
import com.sample.wanandroidclean.domain.usecase.GetUserInfoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MineUiState(
    val isLoading: Boolean = false,
    val userInfo: UserInfo? = null,
    val error: String? = null
)

class MineViewModel(private val getUserInfoUseCase: GetUserInfoUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow(MineUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadUserInfo()
    }

    private fun loadUserInfo() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getUserInfoUseCase().fold(
                onSuccess = { userInfo ->
                    _uiState.update { currentState ->
                        currentState.copy(isLoading = false, userInfo = userInfo)
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
