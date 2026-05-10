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
}
