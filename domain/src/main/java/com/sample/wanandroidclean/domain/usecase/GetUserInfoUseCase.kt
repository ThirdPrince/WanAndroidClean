package com.sample.wanandroidclean.domain.usecase

import com.sample.wanandroidclean.domain.entity.UserInfo
import com.sample.wanandroidclean.domain.repository.UserInfoRepository

/**
 * Use case for getting the user info.
 */
class GetUserInfoUseCase(private val repository: UserInfoRepository) {

    suspend operator fun invoke(): Result<UserInfo> = repository.getUserInfo()
}
