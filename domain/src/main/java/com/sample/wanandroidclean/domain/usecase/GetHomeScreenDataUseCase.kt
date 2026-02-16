package com.sample.wanandroidclean.domain.usecase

import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.entity.Banner
import com.sample.wanandroidclean.domain.repository.ArticleRepository
import com.sample.wanandroidclean.domain.repository.BannerRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

// A data class to hold the combined result for the home screen. Fields can be null if a request fails.
data class HomeScreenData(
    val banners: List<Banner>? = null,
    val articles: List<Article>? = null
)

/**
 * Use case for getting the combined data for the home screen (banners and articles).
 * It fetches them in parallel and returns a success result even if one of them fails.
 */
class GetHomeScreenDataUseCase(
    private val bannerRepository: BannerRepository,
    private val articleRepository: ArticleRepository
) {

    suspend operator fun invoke(): Result<HomeScreenData> = coroutineScope {
        val bannersDeferred = async { bannerRepository.getBanners() }
        val articlesDeferred = async { articleRepository.getArticles() }

        val bannersResult = bannersDeferred.await()
        val articlesResult = articlesDeferred.await()

        // Create the data object with successful results, leaving failed ones as null
        val homeScreenData = HomeScreenData(
            banners = bannersResult.getOrNull(),
            articles = articlesResult.getOrNull()
        )

        // Always return success, as the partial data is still useful for the UI.
        Result.success(homeScreenData)
    }
}
