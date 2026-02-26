package com.sample.wanandroidclean.data.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.sample.wanandroidclean.data.remote.*
import com.sample.wanandroidclean.data.repository.*
import com.sample.wanandroidclean.domain.repository.*
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit

val dataModule = module {
    single { CookieStorage(androidContext()) }
    single { PersistentCookieJar(get()) }

    single {
        OkHttpClient.Builder()
            .cookieJar(get<PersistentCookieJar>())
            .build()
    }

    single<ArticleRepository> { ArticleRepositoryImpl(get()) }
    single<BannerRepository> { BannerRepositoryImpl(get()) }
    single<TopArticleRepository> { TopArticleRepositoryImpl(get()) }
    single<SystemRepository> { SystemRepositoryImpl(get()) }
    single<NavigationRepository> { NavigationRepositoryImpl(get()) }
    single<WxArticleRepository> { WxArticleRepositoryImpl(get()) }
    single<ProjectRepository> { ProjectRepositoryImpl(get()) }
    single<UserInfoRepository> { UserInfoRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }

    single<WanAndroidApi> {
        get<Retrofit>().create(WanAndroidApi::class.java)
    }

    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("https://www.wanandroid.com/")
            .client(get<OkHttpClient>())
            .addConverterFactory(get<Json>().asConverterFactory("application/json".toMediaType()))
            .build()
    }

    single {
        Json {
            ignoreUnknownKeys = true
        }
    }
}
