package com.sample.wanandroidclean.domain.repository

import androidx.paging.PagingData
import com.sample.wanandroidclean.domain.entity.Article
import kotlinx.coroutines.flow.Flow

/**
 * Interface for the articles repository.
 */
interface ArticleRepository {

    /**
     * Fetches a flow of paging data for articles, supporting offline-first logic.
     */
    fun getArticlesPaging(): Flow<PagingData<Article>>

    /**
     * Standard fetch for a single page of articles.
     */
    suspend fun getArticles(page: Int): Result<List<Article>>

    /**
     * Updates the collection status in the local database for optimistic UI.
     */
    suspend fun updateLocalCollectStatus(id: Int, collect: Boolean)
}
