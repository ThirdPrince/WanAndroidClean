package com.sample.wanandroidclean.domain.usecase

import com.sample.wanandroidclean.domain.entity.Navigation
import com.sample.wanandroidclean.domain.repository.NavigationRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for getting the list of navigation items.
 */
class GetNavigationUseCase(private val repository: NavigationRepository) {

    operator fun invoke(): Flow<Result<List<Navigation>>> = repository.getNavigation()
}
