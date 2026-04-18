package com.sample.wanandroidclean.domain.repository

import com.sample.wanandroidclean.domain.entity.UserInfo
import kotlinx.coroutines.flow.SharedFlow

/**
 * Interface for the user repository.
 */
interface UserRepository {
    /**
     * Performs login with the given username and password.
     */
    suspend fun login(username: String, password: String): Result<UserInfo>
}
