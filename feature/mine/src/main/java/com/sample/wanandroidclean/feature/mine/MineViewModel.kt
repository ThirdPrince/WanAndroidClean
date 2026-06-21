package com.sample.wanandroidclean.feature.mine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.wanandroidclean.domain.entity.UserInfo
import com.sample.wanandroidclean.domain.repository.UserRepository
import com.sample.wanandroidclean.domain.usecase.GetUserInfoUseCase
import com.sample.wanandroidclean.domain.usecase.LogoutUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class MineUiState(
    val isLoading: Boolean = false,
    val userInfo: UserInfo? = null,
    val error: String? = null
)

class MineViewModel(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val userRepository: UserRepository
) : ViewModel() {

    /**
     * 响应式 UI 状态流。
     * 监听登录状态变化。如果已登录，则自动拉取用户信息。
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<MineUiState> = userRepository.isUserLoggedIn
        .flatMapLatest { isLoggedIn ->
            if (isLoggedIn) {
                flow {
                    emit(MineUiState(isLoading = true))
                    getUserInfoUseCase().fold(
                        onSuccess = { userInfo -> emit(MineUiState(userInfo = userInfo)) },
                        onFailure = { e -> emit(MineUiState(error = e.localizedMessage)) }
                    )
                }
            } else {
                // 未登录状态，直接返回空用户信息
                flowOf(MineUiState(userInfo = null))
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MineUiState(isLoading = true)
        )

    /**
     * 执行退出登录逻辑
     */
    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }
}
