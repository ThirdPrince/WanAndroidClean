package com.sample.wanandroidclean.data.model

import com.sample.wanandroidclean.domain.entity.WxChapter
import kotlinx.serialization.Serializable

@Serializable
data class WxChapterDto(
    val id: Int,
    val name: String
) {
    fun toDomain(): WxChapter = WxChapter(
        id = id,
        name = name
    )
}
