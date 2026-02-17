package com.sample.wanandroidclean.data.repository

import com.sample.wanandroidclean.data.remote.WanAndroidApi
import com.sample.wanandroidclean.data.remote.safeApiCall
import com.sample.wanandroidclean.domain.entity.Navigation
import com.sample.wanandroidclean.domain.repository.NavigationRepository

class NavigationRepositoryImpl(private val wanAndroidApi: WanAndroidApi) : NavigationRepository {

    override suspend fun getNavigation(): Result<List<Navigation>> {
        val result = safeApiCall { wanAndroidApi.getNavigation() }
        return result.map { dtoList -> dtoList.map { it.toDomain() } }
    }
}
