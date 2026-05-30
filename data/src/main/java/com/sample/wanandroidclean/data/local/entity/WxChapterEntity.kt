package com.sample.wanandroidclean.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sample.wanandroidclean.domain.entity.WxChapter

@Entity(tableName = "wx_chapters")
data class WxChapterEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val sortOrder: Int
) {
    fun toDomain(): WxChapter = WxChapter(
        id = id,
        name = name
    )

    companion object {
        fun fromDomain(domain: WxChapter, sortOrder: Int): WxChapterEntity = WxChapterEntity(
            id = domain.id,
            name = domain.name,
            sortOrder = sortOrder
        )
    }
}
