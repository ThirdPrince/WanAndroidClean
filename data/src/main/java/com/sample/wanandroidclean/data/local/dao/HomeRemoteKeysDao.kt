package com.sample.wanandroidclean.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sample.wanandroidclean.data.local.entity.HomeRemoteKeys

@Dao
interface HomeRemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<HomeRemoteKeys>)

    @Query("SELECT * FROM home_remote_keys WHERE articleId = :articleId")
    suspend fun getRemoteKeys(articleId: Int): HomeRemoteKeys?

    @Query("DELETE FROM home_remote_keys")
    suspend fun clearRemoteKeys()
}
