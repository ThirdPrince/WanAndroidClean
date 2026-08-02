package com.sample.wanandroidclean.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.sample.wanandroidclean.data.local.AppDatabase
import com.sample.wanandroidclean.data.local.entity.ArticleEntity
import com.sample.wanandroidclean.data.local.entity.HomeRemoteKeys
import com.sample.wanandroidclean.data.remote.WanAndroidApi

@OptIn(ExperimentalPagingApi::class)
class HomeRemoteMediator(
    private val api: WanAndroidApi,
    private val database: AppDatabase
) : RemoteMediator<Int, ArticleEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ArticleEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: 0
            }
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            // 1. 获取网络数据
            val response = api.getArticles(page)
            val articlesDto = response.data.datas
            
            // 2. 如果是第一页刷新，同步获取置顶文章
            val topArticlesDto = if (loadType == LoadType.REFRESH && page == 0) {
                try { api.getTopArticles().data } catch (e: Exception) { emptyList() }
            } else {
                emptyList()
            }

            val endOfPaginationReached = articlesDto.isEmpty() || page >= response.data.pageCount

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeysDao().clearRemoteKeys()
                    database.articleDao().clearArticlesByCategoryId(0)
                }

                val prevKey = if (page == 0) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                
                val topEntities = topArticlesDto.mapIndexed { index, dto ->
                    ArticleEntity.fromDomain(dto.toDomain(isTop = true), 0, -1, index)
                }
                val articleEntities = articlesDto.mapIndexed { index, dto ->
                    ArticleEntity.fromDomain(dto.toDomain(isTop = false), 0, page, index)
                }

                val allEntities = topEntities + articleEntities
                val keys = allEntities.map {
                    HomeRemoteKeys(articleId = it.id, prevKey = prevKey, nextKey = nextKey)
                }

                database.remoteKeysDao().insertAll(keys)
                database.articleDao().insertAll(allEntities)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ArticleEntity>): HomeRemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { article ->
                database.remoteKeysDao().getRemoteKeys(article.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, ArticleEntity>): HomeRemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { articleId ->
                database.remoteKeysDao().getRemoteKeys(articleId)
            }
        }
    }
    override suspend fun initialize(): InitializeAction {
        // 只有在数据失效或手动刷新时才刷新，切换页面不强制刷新
        return InitializeAction.SKIP_INITIAL_REFRESH
    }
}
