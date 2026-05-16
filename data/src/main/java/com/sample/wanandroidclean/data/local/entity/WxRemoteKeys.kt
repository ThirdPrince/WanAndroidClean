package com.sample.wanandroidclean.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wx_remote_keys")
data class WxRemoteKeys(
    @PrimaryKey val articleId: Int,
    val categoryId: Int, // The chapterId
    val prevKey: Int?,
    val nextKey: Int?
)
