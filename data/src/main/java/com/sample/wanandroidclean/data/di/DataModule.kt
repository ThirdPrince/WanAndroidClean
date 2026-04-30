package com.sample.wanandroidclean.data.di

import androidx.room.Room
import com.sample.wanandroidclean.data.datasource.NavigationLocalDataSource
import com.sample.wanandroidclean.data.datasource.NavigationLocalDataSourceImpl
import com.sample.wanandroidclean.data.datasource.NavigationRemoteDataSource
import com.sample.wanandroidclean.data.datasource.NavigationRemoteDataSourceImpl
import com.sample.wanandroidclean.data.datasource.SystemLocalDataSource
import com.sample.wanandroidclean.data.datasource.SystemLocalDataSourceImpl
import com.sample.wanandroidclean.data.datasource.SystemRemoteDataSource
import com.sample.wanandroidclean.data.datasource.SystemRemoteDataSourceImpl
import com.sample.wanandroidclean.data.local.AppDatabase
import com.sample.wanandroidclean.data.remote.*
import com.sample.wanandroidclean.data.repository.*
import com.sample.wanandroidclean.domain.repository.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

val dataModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "wan_android_db"
        ).fallbackToDestructiveMigration().build()
    }
    single { get<AppDatabase>().systemDao() }
    single { get<AppDatabase>().navigationDao() }
    single<SystemRemoteDataSource> { SystemRemoteDataSourceImpl(get()) }
    single<SystemLocalDataSource> { SystemLocalDataSourceImpl(get()) }
    single<NavigationRemoteDataSource> { NavigationRemoteDataSourceImpl(get()) }
    single<NavigationLocalDataSource> { NavigationLocalDataSourceImpl(get()) }

    single { CoroutineScope(Dispatchers.IO + SupervisorJob()) }
    single { CookieStorage(androidContext(), get()) }
    single { PersistentCookieJar(get()) }

    single {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .cookieJar(get<PersistentCookieJar>())
            .build()
    }

    single<ArticleRepository> { ArticleRepositoryImpl(get()) }
    single<BannerRepository> { BannerRepositoryImpl(get()) }
    single<TopArticleRepository> { TopArticleRepositoryImpl(get()) }
    single<SystemRepository> { SystemRepositoryImpl(get(), get(), get()) }
    single<NavigationRepository> { NavigationRepositoryImpl(get(), get()) }
    single<WxArticleRepository> { WxArticleRepositoryImpl(get()) }
    single<ProjectRepository> { ProjectRepositoryImpl(get()) }
    single<UserInfoRepository> { UserInfoRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<CollectionRepository> { CollectionRepositoryImpl(get()) }

    single<WanAndroidApi> {
        get<Retrofit>().create(WanAndroidApi::class.java)
    }

    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("https://wanandroid.com/")
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
