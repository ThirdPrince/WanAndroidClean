package com.sample.wanandroidclean.data.remote

import com.sample.wanandroidclean.data.model.ArticleResponse
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Defines the API for WanAndroid.
 */
interface WanAndroidApi {

    @GET("article/list/{page}/json")
    suspend fun getArticles(@Path("page") page: Int): ArticleResponse
}
