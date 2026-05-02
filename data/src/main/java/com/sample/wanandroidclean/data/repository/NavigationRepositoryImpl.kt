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
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NavigationRepositoryImpl(
    private val remoteDataSource: NavigationRemoteDataSource,
    private val localDataSource: NavigationLocalDataSource
) : NavigationRepository {

    override fun getNavigation(): Flow<Result<List<Navigation>>> = channelFlow {
        // 1. 启动一个协程持续观察本地数据库变化
        val localJob = launch {
            localDataSource.getNavigation().collect { localData ->
                // 只要数据库有变动，立即发送给 UI
                send(Result.success(localData.toDomain()))
            }
        }

        // 2. 发起网络请求
        val remoteResult = remoteDataSource.getNavigation()
        if (remoteResult.isSuccess) {
            val remote = remoteResult.getOrThrow()
            // 写入数据库。这次写入会自动触发上面 localJob 的 collect，从而 send 新数据
            localDataSource.saveNavigation(
                navigation = remote.toNavigationEntities(),
                articles = remote.toArticleEntities()
            )
        } else {
            // 网络失败处理：如果本地也没数据，说明是首次进入且没网，发送错误
            val currentLocal = localDataSource.getNavigation().first()
            if (currentLocal.navigation.isEmpty()) {
                send(
                    Result.failure(
                        remoteResult.exceptionOrNull() ?: Exception("Network failed and no cache available")
                    )
                )
            }
        }

        // 保持通道开启，直到 UI 停止监听（比如页面销毁）
        awaitClose { localJob.cancel() }
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
