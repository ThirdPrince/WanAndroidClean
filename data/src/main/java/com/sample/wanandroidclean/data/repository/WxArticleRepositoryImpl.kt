package com.sample.wanandroidclean.data.repository

import com.sample.wanandroidclean.data.remote.WanAndroidApi
import com.sample.wanandroidclean.data.remote.safeApiCall
import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.entity.WxChapter
import com.sample.wanandroidclean.domain.repository.WxArticleRepository

class WxArticleRepositoryImpl(private val wanAndroidApi: WanAndroidApi) : WxArticleRepository {

    override suspend fun getWxChapters(): Result<List<WxChapter>> {
        val result = safeApiCall { wanAndroidApi.getWxChapters() }
        return result.map { dtoList -> dtoList.map { it.toDomain() } }
    }

    override suspend fun getWxArticles(chapterId: Int, page: Int): Result<List<Article>> {
        val result = safeApiCall { wanAndroidApi.getWxArticles(chapterId, page) }
        return result.map { articleData -> articleData.datas.map { it.toDomain() } }
    }
}
