package com.sample.wanandroidclean.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sample.wanandroidclean.data.local.dao.SystemDao
import com.sample.wanandroidclean.data.local.entity.SystemCategoryEntity

@Database(entities = [SystemCategoryEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun systemDao(): SystemDao
}
