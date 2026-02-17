package com.sample.wanandroidclean.data.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.sample.wanandroidclean.data.remote.WanAndroidApi
import com.sample.wanandroidclean.data.repository.ArticleRepositoryImpl
import com.sample.wanandroidclean.data.repository.BannerRepositoryImpl
import com.sample.wanandroidclean.data.repository.NavigationRepositoryImpl
import com.sample.wanandroidclean.data.repository.SystemRepositoryImpl
import com.sample.wanandroidclean.data.repository.TopArticleRepositoryImpl
import com.sample.wanandroidclean.domain.repository.ArticleRepository
import com.sample.wanandroidclean.domain.repository.BannerRepository
import com.sample.wanandroidclean.domain.repository.NavigationRepository
import com.sample.wanandroidclean.domain.repository.SystemRepository
import com.sample.wanandroidclean.domain.repository.TopArticleRepository
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import org.koin.dsl.module
import retrofit2.Retrofit

val dataModule = module {
    single<ArticleRepository> { ArticleRepositoryImpl(get()) }
    single<BannerRepository> { BannerRepositoryImpl(get()) }
    single<TopArticleRepository> { TopArticleRepositoryImpl(get()) }
    single<SystemRepository> { SystemRepositoryImpl(get()) }
    single<NavigationRepository> { NavigationRepositoryImpl(get()) }

    single<WanAndroidApi> {
        val retrofit = get<Retrofit>()
        retrofit.create(WanAndroidApi::class.java)
    }

    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("https://www.wanandroid.com/")
            .addConverterFactory(get<Json>().asConverterFactory("application/json".toMediaType()))
            .build()
    }

    single {
        Json {
            ignoreUnknownKeys = true
        }
    }
}
