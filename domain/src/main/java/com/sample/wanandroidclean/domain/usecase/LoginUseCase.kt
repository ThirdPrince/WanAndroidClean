package com.sample.wanandroidclean.domain.usecase

import com.sample.wanandroidclean.domain.entity.UserInfo
import com.sample.wanandroidclean.domain.repository.UserRepository

/**
 * Use case for logging in a user.
 */
class LoginUseCase(private val repository: UserRepository) {

    suspend operator fun invoke(username: String, password: String): Result<UserInfo> {
        // Here we could add some business rules (e.g. username length check)
        if (username.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Username or password cannot be empty"))
        }
        return repository.login(username, password)
    }
}
