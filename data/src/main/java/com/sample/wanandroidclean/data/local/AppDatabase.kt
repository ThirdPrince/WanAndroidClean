package com.sample.wanandroidclean.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sample.wanandroidclean.data.local.dao.*
import com.sample.wanandroidclean.data.local.entity.*

@Database(
    entities = [
        SystemCategoryEntity::class,
        NavigationEntity::class,
        NavigationArticleEntity::class,
        ArticleEntity::class,
        HomeRemoteKeys::class,
        BannerEntity::class,
        WxRemoteKeys::class
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun systemDao(): SystemDao
    abstract fun navigationDao(): NavigationDao
    abstract fun articleDao(): ArticleDao
    abstract fun remoteKeysDao(): HomeRemoteKeysDao
    abstract fun bannerDao(): BannerDao
    abstract fun wxRemoteKeysDao(): WxRemoteKeysDao
}
