package com.sample.wanandroidclean.data.repository

import com.sample.wanandroidclean.data.datasource.SystemLocalDataSource
import com.sample.wanandroidclean.data.datasource.SystemRemoteDataSource
import com.sample.wanandroidclean.data.local.entity.SystemCategoryEntity
import com.sample.wanandroidclean.data.model.SystemCategoryDto
import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.entity.SystemCategory
import com.sample.wanandroidclean.domain.repository.SystemRepository
import com.sample.wanandroidclean.data.remote.WanAndroidApi
import com.sample.wanandroidclean.data.remote.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class SystemRepositoryImpl(
    private val remoteDataSource: SystemRemoteDataSource,
    private val localDataSource: SystemLocalDataSource,
    private val wanAndroidApi: WanAndroidApi // 仍然需要 API 来获取文章列表
) : SystemRepository {

    override fun getSystemCategories(): Flow<Result<List<SystemCategory>>> = flow {
        // 1. 先读取本地缓存
        val localCategories = localDataSource.getSystemCategories().first()
        val hasLocalCache = localCategories.isNotEmpty()

        if (hasLocalCache) {
            emit(Result.success(localCategories.toDomainTree()))
        } else {
            emit(Result.success(emptyList()))
        }

        // 2. 再请求远程，成功后刷新本地缓存
        val remoteResult = remoteDataSource.getSystemCategories()

        if (remoteResult.isSuccess) {
            localDataSource.saveSystemCategories(remoteResult.getOrThrow().toEntities())
            emit(Result.success(localDataSource.getSystemCategories().first().toDomainTree()))
        } else if (!hasLocalCache) {
            emit(
                Result.failure(
                    remoteResult.exceptionOrNull() ?: Exception("Unknown error")
                )
            )
        } else {
            emit(Result.success(localCategories.toDomainTree()))
        }
    }

    override suspend fun getSystemArticles(page: Int, cid: Int): Result<List<Article>> {
        // 获取特定分类下的文章，由于变动频繁且量大，目前保持纯远程获取
        val result = safeApiCall { wanAndroidApi.getSystemArticles(page, cid) }
        return result.map { articleData -> articleData.datas.map { it.toDomain() } }
    }

    private fun List<SystemCategoryDto>.toEntities(parentId: Int? = null): List<SystemCategoryEntity> {
        return flatMap { dto ->
            listOf(
                SystemCategoryEntity(
                    id = dto.id,
                    name = dto.name,
                    parentId = parentId
                )
            ) + dto.children.toEntities(dto.id)
        }
    }

    private fun List<SystemCategoryEntity>.toDomainTree(): List<SystemCategory> {
        val childrenByParentId = groupBy { it.parentId }

        fun build(parentId: Int?): List<SystemCategory> {
            return childrenByParentId[parentId].orEmpty().map { entity ->
                SystemCategory(
                    id = entity.id,
                    name = entity.name,
                    children = build(entity.id)
                )
            }
        }

        return build(parentId = null)
    }
}
