package com.sample.wanandroidclean.domain.usecase

import com.sample.wanandroidclean.domain.entity.Banner
import com.sample.wanandroidclean.domain.repository.BannerRepository

/**
 * Use case for getting the list of banners.
 */
class GetBannersUseCase(private val bannerRepository: BannerRepository) {

    /**
     * Executes the use case.
     */
    suspend operator fun invoke(): Result<List<Banner>> = bannerRepository.getBanners()
}
