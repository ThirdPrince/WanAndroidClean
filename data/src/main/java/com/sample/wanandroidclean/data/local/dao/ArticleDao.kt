package com.sample.wanandroidclean.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sample.wanandroidclean.data.local.entity.ArticleEntity

@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articles: List<ArticleEntity>)

    @Query("SELECT * FROM articles WHERE categoryId = :categoryId ORDER BY page ASC, orderInPage ASC")
    fun getArticlesByCategoryId(categoryId: Int): PagingSource<Int, ArticleEntity>

    // 关键：手动更新收藏状态，用于即时反馈高亮
    @Query("UPDATE articles SET collect = :collect WHERE id = :articleId")
    suspend fun updateCollectStatus(articleId: Int, collect: Boolean)

    @Query("DELETE FROM articles WHERE categoryId = :categoryId")
    suspend fun clearArticlesByCategoryId(categoryId: Int)

    @Query("DELETE FROM articles")
    suspend fun clearAll()
}
