package com.sample.wanandroidclean.domain.repository

import com.sample.wanandroidclean.domain.entity.Article

/**
 * Interface for the top articles repository.
 */
interface TopArticleRepository {

    /**
     * Fetches the list of top articles from the data source.
     */
    suspend fun getTopArticles(): Result<List<Article>>
}
