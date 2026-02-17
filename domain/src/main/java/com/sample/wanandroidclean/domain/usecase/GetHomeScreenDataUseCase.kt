package com.sample.wanandroidclean.domain.usecase

import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.entity.Banner
import com.sample.wanandroidclean.domain.repository.ArticleRepository
import com.sample.wanandroidclean.domain.repository.BannerRepository
import com.sample.wanandroidclean.domain.repository.TopArticleRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

// A data class to hold the combined result for the home screen. Fields can be null if a request fails.
data class HomeScreenData(
    val banners: List<Banner>? = null,
    val articles: List<Article>? = null
)

/**
 * Use case for getting the combined data for the home screen.
 * It fetches banners, top articles, and normal articles in parallel.
 */
class GetHomeScreenDataUseCase(
    private val bannerRepository: BannerRepository,
    private val topArticleRepository: TopArticleRepository,
    private val articleRepository: ArticleRepository
) {

    suspend operator fun invoke(): Result<HomeScreenData> = coroutineScope {
        val bannersDeferred = async { bannerRepository.getBanners() }
        val topArticlesDeferred = async { topArticleRepository.getTopArticles() }
        val normalArticlesDeferred = async { articleRepository.getArticles(0) } // Page 0 for now

        val bannersResult = bannersDeferred.await()
        val topArticlesResult = topArticlesDeferred.await()
        val normalArticlesResult = normalArticlesDeferred.await()
        
        val topArticles = topArticlesResult.getOrNull() ?: emptyList()
        val normalArticles = normalArticlesResult.getOrNull() ?: emptyList()
        val combinedArticles = topArticles + normalArticles

        // If all requests fail, the entire operation is a failure.
        if (bannersResult.isFailure && topArticlesResult.isFailure && normalArticlesResult.isFailure) {
            val exception = bannersResult.exceptionOrNull() ?: Exception("Unknown error")
            Result.failure(exception)
        } else {
            val homeScreenData = HomeScreenData(
                banners = bannersResult.getOrNull(),
                articles = combinedArticles.ifEmpty { null }
            )
            Result.success(homeScreenData)
        }
    }
}
