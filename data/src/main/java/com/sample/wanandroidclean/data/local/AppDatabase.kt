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
        WxRemoteKeys::class,
        WxChapterEntity::class,
        ProjectRemoteKeys::class,
        ProjectChapterEntity::class
    ],
    version = 9, // 将版本号从 8 升级到 9
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun systemDao(): SystemDao
    abstract fun navigationDao(): NavigationDao
    abstract fun articleDao(): ArticleDao
    abstract fun remoteKeysDao(): HomeRemoteKeysDao
    abstract fun bannerDao(): BannerDao
    abstract fun wxRemoteKeysDao(): WxRemoteKeysDao
    abstract fun wxChapterDao(): WxChapterDao
    abstract fun projectRemoteKeysDao(): ProjectRemoteKeysDao
    abstract fun projectChapterDao(): ProjectChapterDao
}
