package com.sample.wanandroidclean.domain.usecase

import com.sample.wanandroidclean.domain.entity.Banner
import com.sample.wanandroidclean.domain.repository.BannerRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for getting the list of banners.
 * Updated to support the reactive flow from BannerRepository.
 */
class GetBannersUseCase(private val bannerRepository: BannerRepository) {

    /**
     * Executes the use case and returns a flow of results.
     */
    operator fun invoke(): Flow<Result<List<Banner>>> = bannerRepository.getBanners()
}
