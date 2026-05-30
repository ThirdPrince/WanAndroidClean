package com.sample.wanandroidclean.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "project_remote_keys")
data class ProjectRemoteKeys(
    @PrimaryKey val articleId: Int,
    val categoryId: Int, // chapterId
    val prevKey: Int?,
    val nextKey: Int?
)
