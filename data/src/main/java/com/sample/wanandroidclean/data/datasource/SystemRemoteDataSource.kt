package com.sample.wanandroidclean.data.datasource

import com.sample.wanandroidclean.data.model.SystemCategoryDto
import com.sample.wanandroidclean.data.remote.WanAndroidApi

interface SystemRemoteDataSource {
    suspend fun getSystemCategories(): Result<List<SystemCategoryDto>>
}

class SystemRemoteDataSourceImpl(private val api: WanAndroidApi) : SystemRemoteDataSource {
    override suspend fun getSystemCategories(): Result<List<SystemCategoryDto>> {
        return com.sample.wanandroidclean.data.remote.safeApiCall { api.getSystemCategories() }
    }
}
