package com.sample.wanandroidclean.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity representing a system category in Room using Adjacency List pattern.
 */
@Entity(
    tableName = "system_category",
    foreignKeys = [
        ForeignKey(
            entity = SystemCategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["parent_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("parent_id")
    ]
)
data class SystemCategoryEntity(
    @PrimaryKey val id: Int,
    val name: String,
    @ColumnInfo(name = "parent_id") val parentId: Int?
)
