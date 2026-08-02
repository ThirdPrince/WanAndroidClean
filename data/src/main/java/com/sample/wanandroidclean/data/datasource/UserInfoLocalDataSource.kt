package com.sample.wanandroidclean.data.datasource

import com.sample.wanandroidclean.data.local.dao.UserInfoDao
import com.sample.wanandroidclean.data.local.entity.UserInfoEntity
import kotlinx.coroutines.flow.Flow

interface UserInfoLocalDataSource {
    fun getUserInfo(): Flow<UserInfoEntity?>
    suspend fun saveUserInfo(userInfo: UserInfoEntity)
    suspend fun clearUserInfo()
}

class UserInfoLocalDataSourceImpl(private val userInfoDao: UserInfoDao) : UserInfoLocalDataSource {
    override fun getUserInfo(): Flow<UserInfoEntity?> = userInfoDao.getUserInfo()

    override suspend fun saveUserInfo(userInfo: UserInfoEntity) {
        userInfoDao.insertUserInfo(userInfo)
    }

    override suspend fun clearUserInfo() {
        userInfoDao.clearUserInfo()
    }
}
