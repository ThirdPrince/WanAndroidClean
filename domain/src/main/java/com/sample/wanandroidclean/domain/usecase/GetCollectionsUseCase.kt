package com.sample.wanandroidclean.domain.usecase

import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.repository.CollectionRepository

/**
 * Use case for getting the list of collected articles.
 */
class GetCollectionsUseCase(private val repository: CollectionRepository) {

    suspend operator fun invoke(page: Int): Result<List<Article>> = repository.getCollections(page)
}
