package com.sample.wanandroidclean.data.datasource

import com.sample.wanandroidclean.data.local.dao.NavigationDao
import com.sample.wanandroidclean.data.local.entity.NavigationArticleEntity
import com.sample.wanandroidclean.data.local.entity.NavigationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

data class NavigationLocalData(
    val navigation: List<NavigationEntity>,
    val articles: List<NavigationArticleEntity>
)

interface NavigationLocalDataSource {
    fun getNavigation(): Flow<NavigationLocalData>
    suspend fun saveNavigation(
        navigation: List<NavigationEntity>,
        articles: List<NavigationArticleEntity>
    )
}

class NavigationLocalDataSourceImpl(
    private val navigationDao: NavigationDao
) : NavigationLocalDataSource {
    override fun getNavigation(): Flow<NavigationLocalData> {
        return combine(
            navigationDao.getNavigation(),
            navigationDao.getNavigationArticles()
        ) { navigation, articles ->
            NavigationLocalData(
                navigation = navigation,
                articles = articles
            )
        }
    }

    override suspend fun saveNavigation(
        navigation: List<NavigationEntity>,
        articles: List<NavigationArticleEntity>
    ) {
        navigationDao.refreshNavigation(navigation, articles)
    }
}
