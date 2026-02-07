package com.sample.wanandroidclean.data.repository

import com.sample.wanandroidclean.data.remote.WanAndroidApi
import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.repository.ArticleRepository

/**
 * Implementation of the ArticleRepository interface.
 */
class ArticleRepositoryImpl(private val wanAndroidApi: WanAndroidApi) : ArticleRepository {

    override suspend fun getArticles(): List<Article> {
        return wanAndroidApi.getArticles().data.datas.map { it.toDomain() }
    }
}
