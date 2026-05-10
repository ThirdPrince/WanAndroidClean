package com.sample.wanandroidclean.domain.repository

import com.sample.wanandroidclean.domain.entity.Banner
import kotlinx.coroutines.flow.Flow

/**
 * Interface for the banner repository.
 */
interface BannerRepository {
    /**
     * Returns a flow of banners, supporting offline-first logic.
     */
    fun getBanners(): Flow<Result<List<Banner>>>
}
