package com.sample.wanandroidclean.data.datasource

import com.sample.wanandroidclean.data.local.dao.WxChapterDao
import com.sample.wanandroidclean.data.local.entity.WxChapterEntity
import kotlinx.coroutines.flow.Flow

interface WxLocalDataSource {
    fun getWxChapters(): Flow<List<WxChapterEntity>>
    suspend fun saveWxChapters(chapters: List<WxChapterEntity>)
}

class WxLocalDataSourceImpl(private val wxChapterDao: WxChapterDao) : WxLocalDataSource {
    override fun getWxChapters(): Flow<List<WxChapterEntity>> = wxChapterDao.getWxChapters()

    override suspend fun saveWxChapters(chapters: List<WxChapterEntity>) {
        wxChapterDao.refreshWxChapters(chapters)
    }
}
