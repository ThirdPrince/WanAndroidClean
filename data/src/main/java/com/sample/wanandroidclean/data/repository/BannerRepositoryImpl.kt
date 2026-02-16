package com.sample.wanandroidclean.data.repository

import com.sample.wanandroidclean.data.remote.WanAndroidApi
import com.sample.wanandroidclean.data.remote.safeApiCall
import com.sample.wanandroidclean.domain.entity.Banner
import com.sample.wanandroidclean.domain.repository.BannerRepository

class BannerRepositoryImpl(private val wanAndroidApi: WanAndroidApi) : BannerRepository {

    override suspend fun getBanners(): Result<List<Banner>> {
        val bannerResult =
            safeApiCall { wanAndroidApi.getBanners() }
        return bannerResult.map { bannerDto ->
            bannerDto.map { it.toDomain() }
        }
    }

}
