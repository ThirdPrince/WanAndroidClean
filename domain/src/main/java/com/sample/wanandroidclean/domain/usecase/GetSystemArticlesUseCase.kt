package com.sample.wanandroidclean.domain.usecase

import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.repository.SystemRepository

/**
 * Use case for getting the list of articles for a specific system category.
 */
class GetSystemArticlesUseCase(private val repository: SystemRepository) {
    suspend operator fun invoke(page: Int, cid: Int): Result<List<Article>> =
        repository.getSystemArticles(page, cid)
}
