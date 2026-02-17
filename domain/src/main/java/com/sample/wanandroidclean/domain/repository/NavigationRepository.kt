package com.sample.wanandroidclean.domain.repository

import com.sample.wanandroidclean.domain.entity.Navigation

/**
 * Interface for the navigation repository.
 */
interface NavigationRepository {

    /**
     * Fetches the list of navigation categories from the data source.
     */
    suspend fun getNavigation(): Result<List<Navigation>>
}
