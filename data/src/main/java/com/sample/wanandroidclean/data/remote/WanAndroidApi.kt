package com.sample.wanandroidclean.data.remote

import com.sample.wanandroidclean.data.model.ArticleData
import com.sample.wanandroidclean.data.model.ArticleDto
import com.sample.wanandroidclean.data.model.BannerDto
import com.sample.wanandroidclean.data.model.BaseResponse
import com.sample.wanandroidclean.data.model.NavigationDto
import com.sample.wanandroidclean.data.model.ProjectChapterDto
import com.sample.wanandroidclean.data.model.SystemCategoryDto
import com.sample.wanandroidclean.data.model.WxChapterDto
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

    @GET("tree/json")
    suspend fun getSystemCategories(): BaseResponse<List<SystemCategoryDto>>

    @GET("navi/json")
    suspend fun getNavigation(): BaseResponse<List<NavigationDto>>

    @GET("wxarticle/chapters/json")
    suspend fun getWxChapters(): BaseResponse<List<WxChapterDto>>

    @GET("wxarticle/list/{chapterId}/{page}/json")
    suspend fun getWxArticles(@Path("chapterId") chapterId: Int, @Path("page") page: Int): BaseResponse<ArticleData>

    @GET("project/tree/json")
    suspend fun getProjectChapters(): BaseResponse<List<ProjectChapterDto>>
}
