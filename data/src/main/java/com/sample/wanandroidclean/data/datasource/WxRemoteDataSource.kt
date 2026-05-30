package com.sample.wanandroidclean.data.datasource

import com.sample.wanandroidclean.data.model.WxChapterDto
import com.sample.wanandroidclean.data.remote.WanAndroidApi
import com.sample.wanandroidclean.data.remote.safeApiCall

interface WxRemoteDataSource {
    suspend fun getWxChapters(): Result<List<WxChapterDto>>
}

class WxRemoteDataSourceImpl(private val api: WanAndroidApi) : WxRemoteDataSource {
    override suspend fun getWxChapters(): Result<List<WxChapterDto>> {
        return safeApiCall { api.getWxChapters() }
    }
}
