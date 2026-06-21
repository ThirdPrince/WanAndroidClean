package com.sample.wanandroidclean.data.repository

import com.sample.wanandroidclean.data.local.AppDatabase
import com.sample.wanandroidclean.data.remote.WanAndroidApi
import com.sample.wanandroidclean.data.remote.safeApiCall
import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.repository.CollectionRepository

class CollectionRepositoryImpl(
    private val wanAndroidApi: WanAndroidApi,
    private val database: AppDatabase
) : CollectionRepository {

    override suspend fun getCollections(page: Int): Result<List<Article>> {
        val result = safeApiCall { wanAndroidApi.getCollections(page) }
        return result.map { articleData ->
            articleData.datas.map { it.toDomain() }
        }
    }

    override suspend fun collect(id: Int): Result<Unit> {
        val result = safeApiCall { wanAndroidApi.collect(id) }
        return result.onSuccess {
            // 关键：接口成功后，立即更新本地数据库状态
            database.articleDao().updateCollectStatus(id, true)
        }.map { Unit }
    }

    override suspend fun uncollect(id: Int): Result<Unit> {
        val result = safeApiCall { wanAndroidApi.uncollect(id) }
        return result.onSuccess {
            // 关键：接口成功后，立即取消本地高亮
            database.articleDao().updateCollectStatus(id, false)
        }.map { Unit }
    }
}
