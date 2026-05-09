package com.sample.wanandroidclean.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sample.wanandroidclean.data.local.dao.ArticleDao
import com.sample.wanandroidclean.data.local.dao.HomeRemoteKeysDao
import com.sample.wanandroidclean.data.local.dao.NavigationDao
import com.sample.wanandroidclean.data.local.dao.SystemDao
import com.sample.wanandroidclean.data.local.entity.ArticleEntity
import com.sample.wanandroidclean.data.local.entity.HomeRemoteKeys
import com.sample.wanandroidclean.data.local.entity.NavigationArticleEntity
import com.sample.wanandroidclean.data.local.entity.NavigationEntity
import com.sample.wanandroidclean.data.local.entity.SystemCategoryEntity

@Database(
    entities = [
        SystemCategoryEntity::class,
        NavigationEntity::class,
        NavigationArticleEntity::class,
        ArticleEntity::class,
        HomeRemoteKeys::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun systemDao(): SystemDao
    abstract fun navigationDao(): NavigationDao
    abstract fun articleDao(): ArticleDao
    abstract fun remoteKeysDao(): HomeRemoteKeysDao
}
