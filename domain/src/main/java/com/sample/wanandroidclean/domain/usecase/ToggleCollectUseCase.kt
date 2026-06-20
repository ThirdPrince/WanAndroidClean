package com.sample.wanandroidclean.domain.usecase

import com.sample.wanandroidclean.domain.repository.CollectionRepository

/**
 * Use case to toggle the collection status of an article.
 */
class ToggleCollectUseCase(private val repository: CollectionRepository) {
    suspend operator fun invoke(id: Int, collect: Boolean): Result<Unit> {
        return if (collect) {
            repository.collect(id)
        } else {
            repository.uncollect(id)
        }
    }
}
