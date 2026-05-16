package com.sample.wanandroidclean.data.di

import androidx.room.Room
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.sample.wanandroidclean.data.datasource.*
import com.sample.wanandroidclean.data.local.AppDatabase
import com.sample.wanandroidclean.data.remote.*
import com.sample.wanandroidclean.data.repository.*
import com.sample.wanandroidclean.domain.repository.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

val dataModule = module {
    // 1. Database & Dao
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "wan_android_db"
        ).fallbackToDestructiveMigration().build()
    }
    single { get<AppDatabase>().systemDao() }
    single { get<AppDatabase>().navigationDao() }
    single { get<AppDatabase>().articleDao() }
    single { get<AppDatabase>().remoteKeysDao() }
    single { get<AppDatabase>().bannerDao() }
    single { get<AppDatabase>().wxRemoteKeysDao() }

    // 2. DataSources
    single<SystemRemoteDataSource> { SystemRemoteDataSourceImpl(get()) }
    single<SystemLocalDataSource> { SystemLocalDataSourceImpl(get()) }
    single<NavigationRemoteDataSource> { NavigationRemoteDataSourceImpl(get()) }
    single<NavigationLocalDataSource> { NavigationLocalDataSourceImpl(get()) }
    single<BannerRemoteDataSource> { BannerRemoteDataSourceImpl(get()) }
    single<BannerLocalDataSource> { BannerLocalDataSourceImpl(get()) }

    // 3. Infrastructure
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

    // 4. Coil ImageLoader 配置 (支持强力离线缓存)
    single {
        val forceCacheInterceptor = Interceptor { chain ->
            val response = chain.proceed(chain.request())
            response.newBuilder()
                .header("Cache-Control", "public, max-age=2592000")
                .removeHeader("Pragma")
                .build()
        }

        ImageLoader.Builder(androidContext())
            .memoryCache {
                MemoryCache.Builder(androidContext())
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(androidContext().cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.02)
                    .build()
            }
            .okHttpClient {
                get<OkHttpClient>().newBuilder()
                    .addNetworkInterceptor(forceCacheInterceptor)
                    .build()
            }
            .build()
    }

    // 5. Repositories
    single<ArticleRepository> { ArticleRepositoryImpl(get(), get()) }
    single<BannerRepository> { BannerRepositoryImpl(get(), get()) }
    single<TopArticleRepository> { TopArticleRepositoryImpl(get()) }
    single<SystemRepository> { SystemRepositoryImpl(get(), get(), get()) }
    single<NavigationRepository> { NavigationRepositoryImpl(get(), get()) }
    single<WxArticleRepository> { WxArticleRepositoryImpl(get(), get()) }
    single<ProjectRepository> { ProjectRepositoryImpl(get()) }
    single<UserInfoRepository> { UserInfoRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<CollectionRepository> { CollectionRepositoryImpl(get()) }

    // 6. Network
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
