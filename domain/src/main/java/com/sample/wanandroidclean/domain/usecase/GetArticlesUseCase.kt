package com.sample.wanandroidclean.domain.usecase

import androidx.paging.PagingData
import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.repository.ArticleRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for getting paginated articles for the home screen.
 * Updated to support Paging 3 and match the new repository interface.
 */
class GetArticlesUseCase(private val articleRepository: ArticleRepository) {

    /**
     * Returns a flow of paging data for articles.
     */
    operator fun invoke(): Flow<PagingData<Article>> = articleRepository.getArticlesPaging()
}
