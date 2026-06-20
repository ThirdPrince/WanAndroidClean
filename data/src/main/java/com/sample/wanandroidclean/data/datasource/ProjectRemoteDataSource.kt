package com.sample.wanandroidclean.data.datasource

import com.sample.wanandroidclean.data.model.ProjectChapterDto
import com.sample.wanandroidclean.data.remote.WanAndroidApi
import com.sample.wanandroidclean.data.remote.safeApiCall

interface ProjectRemoteDataSource {
    suspend fun getProjectChapters(): Result<List<ProjectChapterDto>>
}

class ProjectRemoteDataSourceImpl(private val api: WanAndroidApi) : ProjectRemoteDataSource {
    override suspend fun getProjectChapters(): Result<List<ProjectChapterDto>> {
        return safeApiCall { api.getProjectChapters() }
    }
}
