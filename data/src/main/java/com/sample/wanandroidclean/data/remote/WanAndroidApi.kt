package com.sample.wanandroidclean.data.remote

import com.sample.wanandroidclean.data.model.*
import retrofit2.http.*

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

    @GET("project/list/{page}/json")
    suspend fun getProjectArticles(@Path("page") page: Int, @Query("cid") cid: Int): BaseResponse<ArticleData>
    
    @GET("lg/coin/userinfo/json")
    suspend fun getUserInfo(): BaseResponse<UserInfoDto>

    @FormUrlEncoded
    @POST("user/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): BaseResponse<UserInfoDto>

    @GET("lg/collect/list/{page}/json")
    suspend fun getCollections(@Path("page") page: Int): BaseResponse<ArticleData>

    @POST("lg/collect/{id}/json")
    suspend fun collect(@Path("id") id: Int): BaseResponse<Unit?>

    @POST("lg/uncollect_originId/{id}/json")
    suspend fun uncollect(@Path("id") id: Int): BaseResponse<Unit?>

    // 体系文章列表
    @GET("article/list/{page}/json")
    suspend fun getSystemArticles(@Path("page") page: Int, @Query("cid") cid: Int): BaseResponse<ArticleData>
}
