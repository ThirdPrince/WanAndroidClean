package com.sample.wanandroidclean.domain.repository

import com.sample.wanandroidclean.domain.entity.UserInfo
import kotlinx.coroutines.flow.Flow

/**
 * Interface for the user repository.
 */
interface UserRepository {
    /**
     * Observes the login status. 
     * Returns true if user is logged in (has valid session/info), false otherwise.
     */
    val isUserLoggedIn: Flow<Boolean>

    /**
     * Performs login with the given username and password.
     */
    suspend fun login(username: String, password: String): Result<UserInfo>
}
