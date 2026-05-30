package com.sample.wanandroidclean.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sample.wanandroidclean.data.local.entity.ProjectRemoteKeys

@Dao
interface ProjectRemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<ProjectRemoteKeys>)

    @Query("SELECT * FROM project_remote_keys WHERE articleId = :articleId AND categoryId = :categoryId")
    suspend fun getRemoteKeys(articleId: Int, categoryId: Int): ProjectRemoteKeys?

    @Query("DELETE FROM project_remote_keys WHERE categoryId = :categoryId")
    suspend fun clearRemoteKeys(categoryId: Int)
}
