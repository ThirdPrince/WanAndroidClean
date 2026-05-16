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
import com.sample.wanandroidclean.domain.repository.ArticleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementation of the ArticleRepository interface supporting Paging 3 and offline-first.
 */
class ArticleRepositoryImpl(
    private val api: WanAndroidApi,
    private val database: AppDatabase
) : ArticleRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getArticlesPaging(): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 2,
                enablePlaceholders = false
            ),
            remoteMediator = HomeRemoteMediator(api, database),
            // 修正：使用带 categoryId 的查询方法，首页传入 0
            pagingSourceFactory = { database.articleDao().getArticlesByCategoryId(0) }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

}
