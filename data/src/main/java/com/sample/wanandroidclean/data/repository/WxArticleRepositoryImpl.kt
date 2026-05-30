package com.sample.wanandroidclean.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sample.wanandroidclean.data.datasource.WxLocalDataSource
import com.sample.wanandroidclean.data.datasource.WxRemoteDataSource
import com.sample.wanandroidclean.data.local.AppDatabase
import com.sample.wanandroidclean.data.local.entity.WxChapterEntity
import com.sample.wanandroidclean.data.remote.WanAndroidApi
import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.entity.WxChapter
import com.sample.wanandroidclean.domain.repository.WxArticleRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class WxArticleRepositoryImpl(
    private val remoteDataSource: WxRemoteDataSource,
    private val localDataSource: WxLocalDataSource,
    private val wanAndroidApi: WanAndroidApi,
    private val database: AppDatabase
) : WxArticleRepository {

    override fun getWxChapters(): Flow<Result<List<WxChapter>>> = channelFlow {
        // 1. 监听本地数据库
        val localJob = launch {
            localDataSource.getWxChapters().collect { entities ->
                send(Result.success(entities.map { it.toDomain() }))
            }
        }

        // 2. 从网络刷新
        val remoteResult = remoteDataSource.getWxChapters()
        if (remoteResult.isSuccess) {
            val chaptersDto = remoteResult.getOrThrow()
            val entities = chaptersDto.mapIndexed { index, dto ->
                WxChapterEntity(
                    id = dto.id,
                    name = dto.name,
                    sortOrder = index
                )
            }
            localDataSource.saveWxChapters(entities)
        } else {
            // 网络失败且本地没数据时发送错误
            val currentLocal = localDataSource.getWxChapters().first()
            if (currentLocal.isEmpty()) {
                send(Result.failure(remoteResult.exceptionOrNull() ?: Exception("Unknown error")))
            }
        }

        awaitClose { localJob.cancel() }
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
