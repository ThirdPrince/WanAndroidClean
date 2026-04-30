package com.sample.wanandroidclean.domain.usecase

import com.sample.wanandroidclean.domain.entity.SystemCategory
import com.sample.wanandroidclean.domain.repository.SystemRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for getting the list of system categories.
 */
class GetSystemCategoriesUseCase(private val repository: SystemRepository) {

    operator fun invoke(): Flow<Result<List<SystemCategory>>> = repository.getSystemCategories()
}
