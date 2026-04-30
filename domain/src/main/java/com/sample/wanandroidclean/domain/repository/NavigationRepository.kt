package com.sample.wanandroidclean.domain.repository

import com.sample.wanandroidclean.domain.entity.Navigation
import kotlinx.coroutines.flow.Flow

/**
 * Interface for the navigation repository.
 */
interface NavigationRepository {

    /**
     * Fetches the list of navigation categories from the data source.
     */
    fun getNavigation(): Flow<Result<List<Navigation>>>
}
