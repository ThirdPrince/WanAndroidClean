package com.sample.wanandroidclean.domain.repository

import com.sample.wanandroidclean.domain.entity.Article

/**
 * Interface for the articles repository.
 */
interface ArticleRepository {

    /**
     * Fetches a single page of articles from the data source.
     */
    suspend fun getArticles(page: Int): Result<List<Article>>
}
