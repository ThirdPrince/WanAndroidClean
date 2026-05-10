package com.sample.wanandroidclean.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sample.wanandroidclean.data.local.entity.BannerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BannerDao {
    @Query("SELECT * FROM banners ORDER BY sortOrder ASC")
    fun getBanners(): Flow<List<BannerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBanners(banners: List<BannerEntity>)

    @Query("DELETE FROM banners")
    suspend fun clearBanners()

    @Transaction
    suspend fun refreshBanners(banners: List<BannerEntity>) {
        clearBanners()
        insertBanners(banners)
    }
}
