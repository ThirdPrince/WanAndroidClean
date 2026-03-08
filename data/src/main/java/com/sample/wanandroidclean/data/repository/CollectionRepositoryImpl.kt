package com.sample.wanandroidclean.data.repository

import com.sample.wanandroidclean.data.remote.WanAndroidApi
import com.sample.wanandroidclean.data.remote.safeApiCall
import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.repository.CollectionRepository

class CollectionRepositoryImpl(private val wanAndroidApi: WanAndroidApi) : CollectionRepository {
    override suspend fun getCollections(page: Int): Result<List<Article>> {
        val result = safeApiCall { wanAndroidApi.getCollections(page) }
        return result.map { articleData -> articleData.datas.map { it.toDomain() } }
    }
}
