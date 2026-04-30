package com.sample.wanandroidclean.data.repository

import com.sample.wanandroidclean.data.datasource.NavigationLocalData
import com.sample.wanandroidclean.data.datasource.NavigationLocalDataSource
import com.sample.wanandroidclean.data.datasource.NavigationRemoteDataSource
import com.sample.wanandroidclean.data.local.entity.NavigationArticleEntity
import com.sample.wanandroidclean.data.local.entity.NavigationEntity
import com.sample.wanandroidclean.data.model.NavigationDto
import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.entity.Navigation
import com.sample.wanandroidclean.domain.repository.NavigationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class NavigationRepositoryImpl(
    private val remoteDataSource: NavigationRemoteDataSource,
    private val localDataSource: NavigationLocalDataSource
) : NavigationRepository {

    override fun getNavigation(): Flow<Result<List<Navigation>>> = flow {
        val localNavigation = localDataSource.getNavigation().first()
        val hasLocalCache = localNavigation.navigation.isNotEmpty()

        if (hasLocalCache) {
            emit(Result.success(localNavigation.toDomain()))
        } else {
            emit(Result.success(emptyList()))
        }

        val remoteResult = remoteDataSource.getNavigation()

        if (remoteResult.isSuccess) {
            val remoteNavigation = remoteResult.getOrThrow()
            localDataSource.saveNavigation(
                navigation = remoteNavigation.toNavigationEntities(),
                articles = remoteNavigation.toArticleEntities()
            )
            emit(Result.success(localDataSource.getNavigation().first().toDomain()))
        } else if (!hasLocalCache) {
            emit(Result.failure(remoteResult.exceptionOrNull() ?: Exception("Unknown error")))
        } else {
            emit(Result.success(localNavigation.toDomain()))
        }
    }

    private fun List<NavigationDto>.toNavigationEntities(): List<NavigationEntity> {
        return mapIndexed { index, dto ->
            NavigationEntity(
                id = dto.cid,
                name = dto.name,
                sortOrder = index
            )
        }
    }

    private fun List<NavigationDto>.toArticleEntities(): List<NavigationArticleEntity> {
        return flatMap { navigation ->
            navigation.articles.mapIndexed { index, article ->
                val domainArticle = article.toDomain()
                NavigationArticleEntity(
                    id = domainArticle.id,
                    navigationId = navigation.cid,
                    title = domainArticle.title,
                    author = domainArticle.author,
                    shareUser = domainArticle.shareUser,
                    link = domainArticle.link,
                    sortOrder = index
                )
            }
        }
    }

    private fun NavigationLocalData.toDomain(): List<Navigation> {
        val articlesByNavigationId = articles.groupBy { it.navigationId }

        return navigation.map { navigation ->
            Navigation(
                id = navigation.id,
                name = navigation.name,
                articles = articlesByNavigationId[navigation.id].orEmpty()
                    .sortedBy { it.sortOrder }
                    .map { it.toDomain() }
            )
        }
    }

    private fun NavigationArticleEntity.toDomain(): Article {
        return Article(
            id = id,
            title = title,
            author = author,
            shareUser = shareUser,
            link = link
        )
    }
}
