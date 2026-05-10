package com.sample.wanandroidclean.data.repository

import com.sample.wanandroidclean.data.datasource.BannerLocalDataSource
import com.sample.wanandroidclean.data.datasource.BannerRemoteDataSource
import com.sample.wanandroidclean.data.local.entity.BannerEntity
import com.sample.wanandroidclean.domain.entity.Banner
import com.sample.wanandroidclean.domain.repository.BannerRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BannerRepositoryImpl(
    private val remoteDataSource: BannerRemoteDataSource,
    private val localDataSource: BannerLocalDataSource
) : BannerRepository {

    override fun getBanners(): Flow<Result<List<Banner>>> = channelFlow {
        // 1. Observe local data
        val localJob = launch {
            localDataSource.getBanners().collect { entities ->
                send(Result.success(entities.map { it.toDomain() }))
            }
        }

        // 2. Refresh from remote
        val remoteResult = remoteDataSource.getBanners()
        if (remoteResult.isSuccess) {
            val bannersDto = remoteResult.getOrThrow()
            val entities = bannersDto.mapIndexed { index, dto ->
                BannerEntity(
                    id = dto.id,
                    imagePath = dto.imagePath,
                    title = dto.title,
                    url = dto.url,
                    sortOrder = index
                )
            }
            localDataSource.saveBanners(entities)
        } else {
            // If remote fails and local is empty, emit failure
            val currentLocal = localDataSource.getBanners().first()
            if (currentLocal.isEmpty()) {
                send(Result.failure(remoteResult.exceptionOrNull() ?: Exception("Unknown error")))
            }
        }

        awaitClose { localJob.cancel() }
    }
}
