package com.sample.wanandroidclean.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sample.wanandroidclean.data.local.dao.NavigationDao
import com.sample.wanandroidclean.data.local.dao.SystemDao
import com.sample.wanandroidclean.data.local.entity.NavigationArticleEntity
import com.sample.wanandroidclean.data.local.entity.NavigationEntity
import com.sample.wanandroidclean.data.local.entity.SystemCategoryEntity

@Database(
    entities = [
        SystemCategoryEntity::class,
        NavigationEntity::class,
        NavigationArticleEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun systemDao(): SystemDao
    abstract fun navigationDao(): NavigationDao
}
