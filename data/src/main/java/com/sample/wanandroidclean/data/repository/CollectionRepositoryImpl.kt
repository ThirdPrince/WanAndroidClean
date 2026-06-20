package com.sample.wanandroidclean.data.repository

import com.sample.wanandroidclean.data.remote.WanAndroidApi
import com.sample.wanandroidclean.data.remote.safeApiCall
import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.repository.CollectionRepository

class CollectionRepositoryImpl(private val wanAndroidApi: WanAndroidApi) : CollectionRepository {

    override suspend fun getCollections(page: Int): Result<List<Article>> {
        val result = safeApiCall { wanAndroidApi.getCollections(page) }
        return result.map { articleData ->
            articleData.datas.map { it.toDomain() }
        }
    }

    override suspend fun collect(id: Int): Result<Unit> {
        val result = safeApiCall { wanAndroidApi.collect(id) }
        return result.map { Unit }
    }

    override suspend fun uncollect(id: Int): Result<Unit> {
        val result = safeApiCall { wanAndroidApi.uncollect(id) }
        return result.map { Unit }
    }
}
