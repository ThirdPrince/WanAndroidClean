package com.sample.wanandroidclean.data.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.sample.wanandroidclean.data.remote.WanAndroidApi
import com.sample.wanandroidclean.data.repository.ArticleRepositoryImpl
import com.sample.wanandroidclean.domain.repository.ArticleRepository
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import org.koin.dsl.module
import retrofit2.Retrofit

val dataModule = module {
    single<ArticleRepository> { ArticleRepositoryImpl(get()) }

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
