package com.sample.wanandroidclean.data.repository

import com.sample.wanandroidclean.data.remote.WanAndroidApi
import com.sample.wanandroidclean.domain.entity.Banner
import com.sample.wanandroidclean.domain.repository.BannerRepository

class BannerRepositoryImpl(private val wanAndroidApi: WanAndroidApi) : BannerRepository {

    override suspend fun getBanners(): List<Banner> {
        return wanAndroidApi.getBanners().data.map { it.toDomain() }
    }
}
