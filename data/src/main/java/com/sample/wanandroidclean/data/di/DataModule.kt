package com.sample.wanandroidclean.data.di

import androidx.room.Room
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import com.sample.wanandroidclean.data.datasource.*
import com.sample.wanandroidclean.data.local.AppDatabase
import com.sample.wanandroidclean.data.local.dao.*
import com.sample.wanandroidclean.data.remote.*
import com.sample.wanandroidclean.data.repository.*
import com.sample.wanandroidclean.domain.repository.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.io.File

val dataModule = module {
    // 1. Database
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "wan_android_db"
        ).fallbackToDestructiveMigration().build()
    }

    // 2. DAOs - 显式指定泛型类型，彻底解决不同编译器下的 Cannot infer type 报错
    single<SystemDao> { get<AppDatabase>().systemDao() }
    single<NavigationDao> { get<AppDatabase>().navigationDao() }
    single<ArticleDao> { get<AppDatabase>().articleDao() }
    single<HomeRemoteKeysDao> { get<AppDatabase>().remoteKeysDao() }
    single<BannerDao> { get<AppDatabase>().bannerDao() }
    single<WxRemoteKeysDao> { get<AppDatabase>().wxRemoteKeysDao() }
    single<WxChapterDao> { get<AppDatabase>().wxChapterDao() }
    single<ProjectChapterDao> { get<AppDatabase>().projectChapterDao() }
    single<ProjectRemoteKeysDao> { get<AppDatabase>().projectRemoteKeysDao() }
    single<UserInfoDao> { get<AppDatabase>().userInfoDao() }

    // 3. DataSources
    single<SystemRemoteDataSource> { SystemRemoteDataSourceImpl(get()) }
    single<SystemLocalDataSource> { SystemLocalDataSourceImpl(get()) }
    single<NavigationRemoteDataSource> { NavigationRemoteDataSourceImpl(get()) }
    single<NavigationLocalDataSource> { NavigationLocalDataSourceImpl(get()) }
    single<BannerRemoteDataSource> { BannerRemoteDataSourceImpl(get()) }
    single<BannerLocalDataSource> { BannerLocalDataSourceImpl(get()) }
    single<WxLocalDataSource> { WxLocalDataSourceImpl(get()) }
    single<WxRemoteDataSource> { WxRemoteDataSourceImpl(get()) }
    single<ProjectLocalDataSource> { ProjectLocalDataSourceImpl(get()) }
    single<ProjectRemoteDataSource> { ProjectRemoteDataSourceImpl(get()) }
    single<UserInfoLocalDataSource> { UserInfoLocalDataSourceImpl(get()) }

    // 4. Infrastructure
    single { CoroutineScope(Dispatchers.IO + SupervisorJob()) }
    single { CookieStorage(androidContext(), get()) }
    single { PersistentCookieJar(get()) }

    single {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val httpCacheDirectory = File(androidContext().cacheDir, "http_cache")
        val cache = Cache(httpCacheDirectory, 10L * 1024 * 1024)

        OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(loggingInterceptor)
            .cookieJar(get<PersistentCookieJar>())
            .build()
    }

    // 5. Coil ImageLoader (离线优先核心配置)
    single {
        ImageLoader.Builder(androidContext())
            .memoryCache { MemoryCache.Builder(androidContext()).maxSizePercent(0.25).build() }
            .diskCache {
                DiskCache.Builder()
                    .directory(androidContext().cacheDir.resolve("image_cache"))
                    .maxSizeBytes(100L * 1024 * 1024)
                    .build()
            }
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            .okHttpClient { get<OkHttpClient>() }
            .crossfade(true)
            .build()
    }

    // 6. Repositories - 补全所有参数注入
    single<ArticleRepository> { ArticleRepositoryImpl(get(), get()) }
    single<BannerRepository> { BannerRepositoryImpl(get(), get()) }
    single<TopArticleRepository> { TopArticleRepositoryImpl(get()) }
    single<SystemRepository> { SystemRepositoryImpl(get(), get(), get()) }
    single<NavigationRepository> { NavigationRepositoryImpl(get(), get()) }
    single<WxArticleRepository> { WxArticleRepositoryImpl(get(), get(), get(), get()) }
    single<ProjectRepository> { ProjectRepositoryImpl(get(), get(), get(), get()) }
    single<UserInfoRepository> { UserInfoRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
    single<CollectionRepository> { CollectionRepositoryImpl(get(), get()) }

    // 7. Network
    single<WanAndroidApi> { get<Retrofit>().create(WanAndroidApi::class.java) }

    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("https://www.wanandroid.com/")
            .client(get<OkHttpClient>())
            .addConverterFactory(get<Json>().asConverterFactory("application/json".toMediaType()))
            .build()
    }

    single { Json { ignoreUnknownKeys = true } }
}
