package com.sample.wanandroidclean.data.datasource

import com.sample.wanandroidclean.data.local.dao.SystemDao
import com.sample.wanandroidclean.data.local.entity.SystemCategoryEntity
import kotlinx.coroutines.flow.Flow

interface SystemLocalDataSource {
    fun getSystemCategories(): Flow<List<SystemCategoryEntity>>
    suspend fun saveSystemCategories(entities: List<SystemCategoryEntity>)
}

class SystemLocalDataSourceImpl(private val systemDao: SystemDao) : SystemLocalDataSource {
    override fun getSystemCategories(): Flow<List<SystemCategoryEntity>> {
        // DataSource 只负责返回数据库实体，不负责业务转换
        return systemDao.getAllCategories()
    }

    override suspend fun saveSystemCategories(entities: List<SystemCategoryEntity>) {
        systemDao.refreshCategories(entities)
    }
}
