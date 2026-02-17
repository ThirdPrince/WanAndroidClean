package com.sample.wanandroidclean.domain.usecase

import com.sample.wanandroidclean.domain.entity.Navigation
import com.sample.wanandroidclean.domain.repository.NavigationRepository

/**
 * Use case for getting the list of navigation items.
 */
class GetNavigationUseCase(private val repository: NavigationRepository) {

    suspend operator fun invoke(): Result<List<Navigation>> = repository.getNavigation()
}
