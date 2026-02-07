package com.sample.wanandroidclean.domain.usecase

import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.repository.ArticleRepository

/**
 * Use case for getting the list of articles.
 */
class GetArticlesUseCase(private val articleRepository: ArticleRepository) {

    /**
     * Executes the use case.
     */
    suspend operator fun invoke(): List<Article> = articleRepository.getArticles()
}
