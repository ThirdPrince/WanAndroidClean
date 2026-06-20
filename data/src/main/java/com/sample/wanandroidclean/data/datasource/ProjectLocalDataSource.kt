package com.sample.wanandroidclean.data.datasource

import com.sample.wanandroidclean.data.local.dao.ProjectChapterDao
import com.sample.wanandroidclean.data.local.entity.ProjectChapterEntity
import kotlinx.coroutines.flow.Flow

interface ProjectLocalDataSource {
    fun getProjectChapters(): Flow<List<ProjectChapterEntity>>
    suspend fun saveProjectChapters(chapters: List<ProjectChapterEntity>)
}

class ProjectLocalDataSourceImpl(private val projectChapterDao: ProjectChapterDao) : ProjectLocalDataSource {
    override fun getProjectChapters(): Flow<List<ProjectChapterEntity>> = projectChapterDao.getProjectChapters()

    override suspend fun saveProjectChapters(chapters: List<ProjectChapterEntity>) {
        projectChapterDao.refreshProjectChapters(chapters)
    }
}
