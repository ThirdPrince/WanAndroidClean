package com.sample.wanandroidclean.data.datasource

import com.sample.wanandroidclean.data.model.NavigationDto
import com.sample.wanandroidclean.data.remote.WanAndroidApi
import com.sample.wanandroidclean.data.remote.safeApiCall

interface NavigationRemoteDataSource {
    suspend fun getNavigation(): Result<List<NavigationDto>>
}

class NavigationRemoteDataSourceImpl(
    private val api: WanAndroidApi
) : NavigationRemoteDataSource {
    override suspend fun getNavigation(): Result<List<NavigationDto>> {
        return safeApiCall { api.getNavigation() }
    }
}
