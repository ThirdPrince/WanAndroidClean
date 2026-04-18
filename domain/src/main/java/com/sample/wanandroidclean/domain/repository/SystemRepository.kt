package com.sample.wanandroidclean.domain.repository

import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.entity.SystemCategory

/**
 * Interface for the system repository.
 */
interface SystemRepository {
    /**
     * Fetches the list of system categories.
     */
    suspend fun getSystemCategories(): Result<List<SystemCategory>>

    /**
     * Fetches the list of articles for a specific category id.
     */
    suspend fun getSystemArticles(page: Int, cid: Int): Result<List<Article>>
}
