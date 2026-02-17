package com.sample.wanandroidclean.domain.usecase

import com.sample.wanandroidclean.domain.entity.SystemCategory
import com.sample.wanandroidclean.domain.repository.SystemRepository

/**
 * Use case for getting the list of system categories.
 */
class GetSystemCategoriesUseCase(private val repository: SystemRepository) {

    suspend operator fun invoke(): Result<List<SystemCategory>> = repository.getSystemCategories()
}
