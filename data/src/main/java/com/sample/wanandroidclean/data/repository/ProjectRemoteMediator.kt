package com.sample.wanandroidclean.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.sample.wanandroidclean.data.local.AppDatabase
import com.sample.wanandroidclean.data.local.entity.ArticleEntity
import com.sample.wanandroidclean.data.local.entity.ProjectRemoteKeys
import com.sample.wanandroidclean.data.remote.WanAndroidApi

@OptIn(ExperimentalPagingApi::class)
class ProjectRemoteMediator(
    private val chapterId: Int,
    private val api: WanAndroidApi,
    private val database: AppDatabase
) : RemoteMediator<Int, ArticleEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ArticleEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> 1
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            val response = api.getProjectArticles(page, chapterId)
            val articlesDto = response.data.datas
            val endOfPaginationReached = articlesDto.isEmpty() || page >= response.data.pageCount

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.projectRemoteKeysDao().clearRemoteKeys(chapterId)
                    database.articleDao().clearArticlesByCategoryId(chapterId)
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = articlesDto.map {
                    ProjectRemoteKeys(articleId = it.id, categoryId = chapterId, prevKey = prevKey, nextKey = nextKey)
                }
                database.projectRemoteKeysDao().insertAll(keys)
                
                // 修正：补全 categoryId (chapterId) 参数，并同步 collect 状态入库
                database.articleDao().insertAll(articlesDto.mapIndexed { index, dto ->
                    ArticleEntity.fromDomain(dto.toDomain(), chapterId, page, index)
                })
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ArticleEntity>): ProjectRemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { article ->
                database.projectRemoteKeysDao().getRemoteKeys(article.id, chapterId)
            }
    }
}
