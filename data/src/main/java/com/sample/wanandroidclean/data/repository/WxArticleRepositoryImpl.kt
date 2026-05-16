package com.sample.wanandroidclean.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sample.wanandroidclean.data.local.AppDatabase
import com.sample.wanandroidclean.data.remote.WanAndroidApi
import com.sample.wanandroidclean.data.remote.safeApiCall
import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.entity.WxChapter
import com.sample.wanandroidclean.domain.repository.WxArticleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WxArticleRepositoryImpl(
    private val wanAndroidApi: WanAndroidApi,
    private val database: AppDatabase
) : WxArticleRepository {

    override suspend fun getWxChapters(): Result<List<WxChapter>> {
        val result = safeApiCall { wanAndroidApi.getWxChapters() }
        return result.map { dtoList -> dtoList.map { it.toDomain() } }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getWxArticlesPaging(chapterId: Int): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 2,
                enablePlaceholders = false
            ),
            remoteMediator = WxRemoteMediator(chapterId, wanAndroidApi, database),
            pagingSourceFactory = { database.articleDao().getArticlesByCategoryId(chapterId) }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }
}
