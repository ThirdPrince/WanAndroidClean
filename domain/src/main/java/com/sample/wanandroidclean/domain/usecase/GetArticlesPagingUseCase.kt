package com.sample.wanandroidclean.domain.usecase

import androidx.paging.PagingData
import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.repository.ArticleRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for getting paginated articles.
 */
class GetArticlesPagingUseCase(private val repository: ArticleRepository) {
    operator fun invoke(): Flow<PagingData<Article>> = repository.getArticlesPaging()
}
