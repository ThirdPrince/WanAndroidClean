package com.sample.wanandroidclean.data.repository

import com.sample.wanandroidclean.data.remote.WanAndroidApi
import com.sample.wanandroidclean.data.remote.safeApiCall
import com.sample.wanandroidclean.domain.entity.SystemCategory
import com.sample.wanandroidclean.domain.repository.SystemRepository

class SystemRepositoryImpl(private val wanAndroidApi: WanAndroidApi) : SystemRepository {

    override suspend fun getSystemCategories(): Result<List<SystemCategory>> {
        val result = safeApiCall { wanAndroidApi.getSystemCategories() }
        return result.map { dtoList -> dtoList.map { it.toDomain() } }
    }
}
