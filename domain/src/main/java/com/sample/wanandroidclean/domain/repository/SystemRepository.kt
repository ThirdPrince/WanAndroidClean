package com.sample.wanandroidclean.domain.repository

import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.entity.SystemCategory
import kotlinx.coroutines.flow.Flow

/**
 * Interface for the system repository.
 */
interface SystemRepository {
    /**
     * Fetches the list of system categories.
     */
    fun getSystemCategories(): Flow<Result<List<SystemCategory>>>

    /**
     * Fetches the list of articles for a specific category id.
     */
    suspend fun getSystemArticles(page: Int, cid: Int): Result<List<Article>>
}
