package com.sample.wanandroidclean.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sample.wanandroidclean.data.local.entity.SystemCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SystemDao {
    @Query("SELECT * FROM system_category")
    fun getAllCategories(): Flow<List<SystemCategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<SystemCategoryEntity>)

    @Query("DELETE FROM system_category")
    suspend fun clearCategories()

    @Transaction
    suspend fun refreshCategories(categories: List<SystemCategoryEntity>) {
        clearCategories()
        insertCategories(categories)
    }
}
