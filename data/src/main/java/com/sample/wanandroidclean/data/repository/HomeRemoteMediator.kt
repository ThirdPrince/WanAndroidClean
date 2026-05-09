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
            LoadType.REFRESH -> 0
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            // 1. 获取普通文章
            val response = api.getArticles(page)
            val articlesDto = response.data.datas.toMutableList()
            
            // 2. 如果是第一页，尝试并行获取置顶文章 (仅供展示，实际根据业务需求合并)
            // 注意：为了持久化顺序，置顶文章可以标记 isTop = true 存入 DB
            val topArticlesDto = if (page == 0) {
                try { api.getTopArticles().data } catch (e: Exception) { emptyList() }
            } else {
                emptyList()
            }

            val endOfPaginationReached = articlesDto.isEmpty() || page >= response.data.pageCount

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeysDao().clearRemoteKeys()
                    database.articleDao().clearAll()
                }

                val prevKey = if (page == 0) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                
                // 处理置顶文章 (page 标记为 -1 或特殊处理以确保排在最前)
                val topEntities = topArticlesDto.mapIndexed { index, dto ->
                    ArticleEntity.fromDomain(dto.toDomain(isTop = true), -1, index)
                }
                
                // 处理普通文章
                val articleEntities = articlesDto.mapIndexed { index, dto ->
                    ArticleEntity.fromDomain(dto.toDomain(isTop = false), page, index)
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
}
