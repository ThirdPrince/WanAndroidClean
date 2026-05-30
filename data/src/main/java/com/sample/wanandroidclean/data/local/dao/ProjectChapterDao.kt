package com.sample.wanandroidclean.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sample.wanandroidclean.data.local.entity.ProjectChapterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectChapterDao {
    @Query("SELECT * FROM project_chapters ORDER BY sortOrder ASC")
    fun getProjectChapters(): Flow<List<ProjectChapterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjectChapters(chapters: List<ProjectChapterEntity>)

    @Query("DELETE FROM project_chapters")
    suspend fun clearProjectChapters()

    @Transaction
    suspend fun refreshProjectChapters(chapters: List<ProjectChapterEntity>) {
        clearProjectChapters()
        insertProjectChapters(chapters)
    }
}
