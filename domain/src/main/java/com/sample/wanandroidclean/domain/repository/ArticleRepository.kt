package com.sample.wanandroidclean.domain.repository

import com.sample.wanandroidclean.domain.entity.Article

/**
 * Interface for the articles repository.
 */
interface ArticleRepository {

    /**
     * Fetches the list of articles from the data source.
     */
    suspend fun getArticles(): Result<List<Article>>
}
