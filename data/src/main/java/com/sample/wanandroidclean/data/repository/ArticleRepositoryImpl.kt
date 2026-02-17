package com.sample.wanandroidclean.data.repository

import com.sample.wanandroidclean.data.remote.WanAndroidApi
import com.sample.wanandroidclean.data.remote.safeApiCall
import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.repository.ArticleRepository

/**
 * Implementation of the ArticleRepository interface for normal articles.
 */
class ArticleRepositoryImpl(private val wanAndroidApi: WanAndroidApi) : ArticleRepository {

    override suspend fun getArticles(page: Int): Result<List<Article>> {
        val articleDataResult = safeApiCall { wanAndroidApi.getArticles(page) }
        return articleDataResult.map { articleData ->
            articleData.datas.map { it.toDomain() }
        }
    }
}
