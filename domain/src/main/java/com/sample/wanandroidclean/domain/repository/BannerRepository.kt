package com.sample.wanandroidclean.domain.repository

import com.sample.wanandroidclean.domain.entity.Banner

/**
 * Interface for the banner repository.
 */
interface BannerRepository {

    /**
     * Fetches the list of banners from the data source.
     */
    suspend fun getBanners(): List<Banner>
}
