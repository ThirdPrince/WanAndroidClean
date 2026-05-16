package com.sample.wanandroidclean.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sample.wanandroidclean.data.local.entity.WxRemoteKeys

@Dao
interface WxRemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<WxRemoteKeys>)

    @Query("SELECT * FROM wx_remote_keys WHERE articleId = :articleId AND categoryId = :categoryId")
    suspend fun getRemoteKeys(articleId: Int, categoryId: Int): WxRemoteKeys?

    @Query("DELETE FROM wx_remote_keys WHERE categoryId = :categoryId")
    suspend fun clearRemoteKeys(categoryId: Int)
}
