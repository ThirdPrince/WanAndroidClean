package com.sample.wanandroidclean.domain.usecase

import com.sample.wanandroidclean.domain.repository.UserRepository

/**
 * Use case for logging out the user.
 */
class LogoutUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(): Result<Unit> = repository.logout()
}
