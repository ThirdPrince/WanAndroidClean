package com.sample.wanandroidclean.domain.repository

import com.sample.wanandroidclean.domain.entity.SystemCategory

/**
 * Interface for the system repository.
 */
interface SystemRepository {

    /**
     * Fetches the list of system categories from the data source.
     */
    suspend fun getSystemCategories(): Result<List<SystemCategory>>
}
