package com.sample.wanandroidclean.data.repository

import com.sample.wanandroidclean.data.remote.WanAndroidApi
import com.sample.wanandroidclean.data.remote.safeApiCall
import com.sample.wanandroidclean.domain.entity.UserInfo
import com.sample.wanandroidclean.domain.repository.UserInfoRepository

class UserInfoRepositoryImpl(private val wanAndroidApi: WanAndroidApi) : UserInfoRepository {

    override suspend fun getUserInfo(): Result<UserInfo> {
        val result = safeApiCall { wanAndroidApi.getUserInfo() }
        return result.map { it.toDomain() }
    }
}
