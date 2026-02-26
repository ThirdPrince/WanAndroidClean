package com.sample.wanandroidclean.data.repository

import com.sample.wanandroidclean.data.remote.WanAndroidApi
import com.sample.wanandroidclean.data.remote.safeApiCall
import com.sample.wanandroidclean.domain.entity.UserInfo
import com.sample.wanandroidclean.domain.repository.UserRepository

class UserRepositoryImpl(private val wanAndroidApi: WanAndroidApi) : UserRepository {
    override suspend fun login(username: String, password: String): Result<UserInfo> {
        val result = safeApiCall { wanAndroidApi.login(username, password) }
        return result.map { it.toDomain() }
    }
}
