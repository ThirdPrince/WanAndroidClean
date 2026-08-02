package com.sample.wanandroidclean.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "navigation")
data class NavigationEntity(
    @PrimaryKey val id: Int,
    val name: String,
    @ColumnInfo(name = "sort_order") val sortOrder: Int
)
