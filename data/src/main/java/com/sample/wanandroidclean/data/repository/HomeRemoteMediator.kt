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
            // 1. 获取网络文章数据
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
                // 3. 如果是刷新，清理旧的文章及 Key 缓存
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeysDao().clearRemoteKeys()
                    database.articleDao().clearArticlesByCategoryId(0)
                }

                val prevKey = if (page == 0) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                
                // 4. 转换并持久化文章 (首页 categoryId = 0)
                // 处理置顶文章 (page = -1 以确保排序在最前)
                val topEntities = topArticlesDto.mapIndexed { index, dto ->
                    ArticleEntity.fromDomain(dto.toDomain(isTop = true), 0, -1, index)
                }
                
                // 处理普通文章
                val articleEntities = articlesDto.mapIndexed { index, dto ->
                    ArticleEntity.fromDomain(dto.toDomain(isTop = false), 0, page, index)
                }

                val allEntities = topEntities + articleEntities
                
                // 保存分页状态 Key
                val keys = allEntities.map {
                    HomeRemoteKeys(articleId = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                database.remoteKeysDao().insertAll(keys)
                
                // 写入文章数据库，触发 UI 刷新
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
}
