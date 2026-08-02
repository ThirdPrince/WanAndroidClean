package com.sample.wanandroidclean.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sample.wanandroidclean.data.local.entity.NavigationArticleEntity
import com.sample.wanandroidclean.data.local.entity.NavigationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NavigationDao {
    @Query("SELECT * FROM navigation ORDER BY sort_order")
    fun getNavigation(): Flow<List<NavigationEntity>>

    @Query("SELECT * FROM navigation_article ORDER BY navigation_id, sort_order")
    fun getNavigationArticles(): Flow<List<NavigationArticleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNavigation(navigation: List<NavigationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNavigationArticles(articles: List<NavigationArticleEntity>)

    @Query("DELETE FROM navigation_article")
    suspend fun clearNavigationArticles()

    @Query("DELETE FROM navigation")
    suspend fun clearNavigation()

    @Transaction
    suspend fun refreshNavigation(
        navigation: List<NavigationEntity>,
        articles: List<NavigationArticleEntity>
    ) {
        clearNavigationArticles()
        clearNavigation()
        insertNavigation(navigation)
        insertNavigationArticles(articles)
    }
}
