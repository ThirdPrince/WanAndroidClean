package com.sample.wanandroidclean.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sample.wanandroidclean.data.local.entity.UserInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserInfoDao {
    @Query("SELECT * FROM user_info LIMIT 1")
    fun getUserInfo(): Flow<UserInfoEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserInfo(userInfo: UserInfoEntity)

    @Query("DELETE FROM user_info")
    suspend fun clearUserInfo()
}
