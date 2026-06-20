package com.sample.wanandroidclean.domain.repository

import com.sample.wanandroidclean.domain.entity.Article

/**
 * Interface for managing user collections.
 */
interface CollectionRepository {
    /**
     * Fetches the list of collected articles.
     */
    suspend fun getCollections(page: Int): Result<List<Article>>

    /**
     * Collects an article by its ID.
     */
    suspend fun collect(id: Int): Result<Unit>

    /**
     * Uncollects an article by its ID.
     */
    suspend fun uncollect(id: Int): Result<Unit>
}
