package com.sample.wanandroidclean.data.datasource

import com.sample.wanandroidclean.data.local.dao.BannerDao
import com.sample.wanandroidclean.data.local.entity.BannerEntity
import com.sample.wanandroidclean.data.model.BannerDto
import com.sample.wanandroidclean.data.remote.WanAndroidApi
import com.sample.wanandroidclean.data.remote.safeApiCall
import kotlinx.coroutines.flow.Flow

interface BannerRemoteDataSource {
    suspend fun getBanners(): Result<List<BannerDto>>
}

class BannerRemoteDataSourceImpl(private val api: WanAndroidApi) : BannerRemoteDataSource {
    override suspend fun getBanners(): Result<List<BannerDto>> {
        return safeApiCall { api.getBanners() }
    }
}

interface BannerLocalDataSource {
    fun getBanners(): Flow<List<BannerEntity>>
    suspend fun saveBanners(banners: List<BannerEntity>)
}

class BannerLocalDataSourceImpl(private val bannerDao: BannerDao) : BannerLocalDataSource {
    override fun getBanners(): Flow<List<BannerEntity>> = bannerDao.getBanners()
    
    override suspend fun saveBanners(banners: List<BannerEntity>) {
        bannerDao.refreshBanners(banners)
    }
}
