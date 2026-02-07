package com.sample.wanandroidclean.data.remote

import com.sample.wanandroidclean.data.model.ArticleResponse
import retrofit2.http.GET

/**
 * Defines the API for WanAndroid.
 */
interface WanAndroidApi {

    @GET("article/list/0/json")
    suspend fun getArticles(): ArticleResponse
}
