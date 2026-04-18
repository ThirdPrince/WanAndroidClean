package com.sample.wanandroidclean.feature.mine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.wanandroidclean.domain.entity.UserInfo
import com.sample.wanandroidclean.domain.usecase.GetUserInfoUseCase
import kotlinx.coroutines.flow.*

data class MineUiState(
    val isLoading: Boolean = false,
    val userInfo: UserInfo? = null,
    val error: String? = null
)

class MineViewModel(private val getUserInfoUseCase: GetUserInfoUseCase) : ViewModel() {

    /**
     * 响应式 UI 状态流
     * 当 UI 开始收集 (Collect) 此流时，会自动触发数据加载逻辑
     */
    val uiState: StateFlow<MineUiState> = flow {
        // 发射加载中状态
        emit(MineUiState(isLoading = true))
        
        // 执行获取用户信息逻辑
        getUserInfoUseCase().fold(
            onSuccess = { userInfo ->
                emit(MineUiState(isLoading = false, userInfo = userInfo))
            },
            onFailure = { e ->
                emit(MineUiState(isLoading = false, error = e.localizedMessage ?: "An unknown error occurred"))
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MineUiState(isLoading = true)
    )
}
