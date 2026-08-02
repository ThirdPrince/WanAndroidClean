package com.sample.wanandroidclean.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "navigation_article",
    foreignKeys = [
        ForeignKey(
            entity = NavigationEntity::class,
            parentColumns = ["id"],
            childColumns = ["navigation_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("navigation_id")
    ],
    primaryKeys = ["id", "navigation_id"]
)
data class NavigationArticleEntity(
    val id: Int,
    @ColumnInfo(name = "navigation_id") val navigationId: Int,
    val title: String,
    val author: String,
    @ColumnInfo(name = "share_user") val shareUser: String,
    val link: String,
    @ColumnInfo(name = "sort_order") val sortOrder: Int
)
