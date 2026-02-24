package com.sample.wanandroidclean.data.model

import com.sample.wanandroidclean.domain.entity.ProjectChapter
import kotlinx.serialization.Serializable

@Serializable
data class ProjectChapterDto(
    val id: Int,
    val name: String
) {
    fun toDomain(): ProjectChapter = ProjectChapter(
        id = id,
        name = name
    )
}
