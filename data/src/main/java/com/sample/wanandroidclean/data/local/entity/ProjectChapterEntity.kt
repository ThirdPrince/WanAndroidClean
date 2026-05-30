package com.sample.wanandroidclean.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sample.wanandroidclean.domain.entity.ProjectChapter

@Entity(tableName = "project_chapters")
data class ProjectChapterEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val sortOrder: Int
) {
    fun toDomain(): ProjectChapter = ProjectChapter(
        id = id,
        name = name
    )

    companion object {
        fun fromDomain(domain: ProjectChapter, sortOrder: Int): ProjectChapterEntity = ProjectChapterEntity(
            id = domain.id,
            name = domain.name,
            sortOrder = sortOrder
        )
    }
}
