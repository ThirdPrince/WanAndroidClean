package com.sample.wanandroidclean.data.repository

import com.sample.wanandroidclean.data.remote.WanAndroidApi
import com.sample.wanandroidclean.data.remote.safeApiCall
import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.repository.TopArticleRepository

class TopArticleRepositoryImpl(private val wanAndroidApi: WanAndroidApi) : TopArticleRepository {

    override suspend fun getTopArticles(): Result<List<Article>> {
        val result = safeApiCall { wanAndroidApi.getTopArticles() }
        return result.map { dtoList -> dtoList.map { it.toDomain(isTop = true) } }
    }
}
