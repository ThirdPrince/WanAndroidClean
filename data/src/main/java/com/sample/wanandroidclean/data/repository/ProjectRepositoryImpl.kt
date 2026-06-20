package com.sample.wanandroidclean.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sample.wanandroidclean.data.datasource.ProjectLocalDataSource
import com.sample.wanandroidclean.data.datasource.ProjectRemoteDataSource
import com.sample.wanandroidclean.data.local.AppDatabase
import com.sample.wanandroidclean.data.local.entity.ProjectChapterEntity
import com.sample.wanandroidclean.data.remote.WanAndroidApi
import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.entity.ProjectChapter
import com.sample.wanandroidclean.domain.repository.ProjectRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ProjectRepositoryImpl(
    private val remoteDataSource: ProjectRemoteDataSource,
    private val localDataSource: ProjectLocalDataSource,
    private val wanAndroidApi: WanAndroidApi,
    private val database: AppDatabase
) : ProjectRepository {

    override fun getProjectChapters(): Flow<Result<List<ProjectChapter>>> = channelFlow {
        // 1. Observe local data for immediate display
        val localJob = launch {
            localDataSource.getProjectChapters().collect { entities ->
                send(Result.success(entities.map { it.toDomain() }))
            }
        }

        // 2. Fetch from remote and update local cache
        val remoteResult = remoteDataSource.getProjectChapters()
        if (remoteResult.isSuccess) {
            val chaptersDto = remoteResult.getOrThrow()
            val entities = chaptersDto.mapIndexed { index, dto ->
                ProjectChapterEntity(
                    id = dto.id,
                    name = dto.name,
                    sortOrder = index
                )
            }
            localDataSource.saveProjectChapters(entities)
        } else {
            // Handle error: if local is empty and remote failed, emit failure
            val currentLocal = localDataSource.getProjectChapters().first()
            if (currentLocal.isEmpty()) {
                send(Result.failure(remoteResult.exceptionOrNull() ?: Exception("Unknown error")))
            }
        }

        awaitClose { localJob.cancel() }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getProjectArticlesPaging(chapterId: Int): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 2,
                enablePlaceholders = false
            ),
            remoteMediator = ProjectRemoteMediator(chapterId, wanAndroidApi, database),
            pagingSourceFactory = { database.articleDao().getArticlesByCategoryId(chapterId) }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }
}
