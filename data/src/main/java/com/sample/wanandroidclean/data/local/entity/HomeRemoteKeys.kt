package com.sample.wanandroidclean.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "home_remote_keys")
data class HomeRemoteKeys(
    @PrimaryKey val articleId: Int,
    val prevKey: Int?,
    val nextKey: Int?
)
