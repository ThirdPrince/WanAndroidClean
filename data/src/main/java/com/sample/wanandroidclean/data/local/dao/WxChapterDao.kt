package com.sample.wanandroidclean.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sample.wanandroidclean.data.local.entity.WxChapterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WxChapterDao {
    @Query("SELECT * FROM wx_chapters ORDER BY sortOrder ASC")
    fun getWxChapters(): Flow<List<WxChapterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWxChapters(chapters: List<WxChapterEntity>)

    @Query("DELETE FROM wx_chapters")
    suspend fun clearWxChapters()

    @Transaction
    suspend fun refreshWxChapters(chapters: List<WxChapterEntity>) {
        clearWxChapters()
        insertWxChapters(chapters)
    }
}
