package com.sample.wanandroidclean.data.repository

import com.sample.wanandroidclean.data.remote.WanAndroidApi
import com.sample.wanandroidclean.data.remote.safeApiCall
import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.repository.ArticleRepository

/**
 * Implementation of the ArticleRepository interface.
 */
class ArticleRepositoryImpl(private val wanAndroidApi: WanAndroidApi) : ArticleRepository {

    override suspend fun getArticles(): Result<List<Article>> {
        // TODO: This should be updated to support pagination
        val articleDataResult = safeApiCall { wanAndroidApi.getArticles(0) }
        return articleDataResult.map { articleData ->
            articleData.datas.map { it.toDomain() }
        }
    }
}
