package com.sample.wanandroidclean.data.repository

import com.sample.wanandroidclean.data.remote.WanAndroidApi
import com.sample.wanandroidclean.data.remote.safeApiCall
import com.sample.wanandroidclean.domain.entity.ProjectChapter
import com.sample.wanandroidclean.domain.repository.ProjectRepository

class ProjectRepositoryImpl(private val wanAndroidApi: WanAndroidApi) : ProjectRepository {

    override suspend fun getProjectChapters(): Result<List<ProjectChapter>> {
        val result = safeApiCall { wanAndroidApi.getProjectChapters() }
        return result.map { dtoList -> dtoList.map { it.toDomain() } }
    }
}
