package com.sample.wanandroidclean.data.remote

import com.sample.wanandroidclean.data.model.ArticleData
import com.sample.wanandroidclean.data.model.ArticleDto
import com.sample.wanandroidclean.data.model.BannerDto
import com.sample.wanandroidclean.data.model.BaseResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Defines the API for WanAndroid.
 */
interface WanAndroidApi {

    @GET("article/list/{page}/json")
    suspend fun getArticles(@Path("page") page: Int): BaseResponse<ArticleData>

    @GET("article/top/json")
    suspend fun getTopArticles(): BaseResponse<List<ArticleDto>>

    @GET("banner/json")
    suspend fun getBanners(): BaseResponse<List<BannerDto>>
}
