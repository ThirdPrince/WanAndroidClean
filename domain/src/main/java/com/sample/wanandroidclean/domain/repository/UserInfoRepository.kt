package com.sample.wanandroidclean.domain.repository

import com.sample.wanandroidclean.domain.entity.UserInfo

/**
 * Interface for the user info repository.
 */
interface UserInfoRepository {

    /**
     * Fetches the user info from the data source.
     */
    suspend fun getUserInfo(): Result<UserInfo>
}
