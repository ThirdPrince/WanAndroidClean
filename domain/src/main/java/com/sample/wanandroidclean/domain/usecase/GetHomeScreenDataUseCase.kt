package com.sample.wanandroidclean.domain.usecase

import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.entity.Banner
import com.sample.wanandroidclean.domain.repository.ArticleRepository
import com.sample.wanandroidclean.domain.repository.BannerRepository
import com.sample.wanandroidclean.domain.repository.TopArticleRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 * 首页初始组合数据模型。
 * 字段设为非空，如果请求失败则返回空列表，简化 UI 判断逻辑。
 */
data class HomeScreenData(
    val banners: List<Banner> = emptyList(),
    val articles: List<Article> = emptyList()
)

/**
 * 获取首页初始组合数据的 UseCase。
 * 它并行获取 Banner、置顶文章和第一页普通文章，用于首页的初始快速展示。
 */
class GetHomeScreenDataUseCase(
    private val bannerRepository: BannerRepository,
    private val topArticleRepository: TopArticleRepository,
    private val articleRepository: ArticleRepository
) {

    suspend operator fun invoke(): Result<HomeScreenData> = coroutineScope {
        // 1. 并行发起三个关键请求
        val bannersDeferred = async { bannerRepository.getBanners() }
        val topArticlesDeferred = async { topArticleRepository.getTopArticles() }
        val normalArticlesDeferred = async { articleRepository.getArticles(0) }

        val bannersResult = bannersDeferred.await()
        val topArticlesResult = topArticlesDeferred.await()
        val normalArticlesResult = normalArticlesDeferred.await()
        
        // 2. 提取数据（如果失败则默认为空列表，体现“离线优先/容错”策略）
        val banners = bannersResult.getOrNull() ?: emptyList()
        val topArticles = topArticlesResult.getOrNull() ?: emptyList()
        val normalArticles = normalArticlesResult.getOrNull() ?: emptyList()
        
        // 3. 组合置顶文章和普通文章
        val combinedArticles = topArticles + normalArticles

        // 4. 只要有任何一项关键数据获取成功，我们就认为这次获取是成功的
        if (bannersResult.isFailure && topArticlesResult.isFailure && normalArticlesResult.isFailure) {
            val exception = bannersResult.exceptionOrNull() ?: Exception("All home data requests failed")
            Result.failure(exception)
        } else {
            Result.success(
                HomeScreenData(
                    banners = banners,
                    articles = combinedArticles
                )
            )
        }
    }
}
