package com.sample.wanandroidclean.data.model

import com.sample.wanandroidclean.domain.entity.SystemCategory
import kotlinx.serialization.Serializable

@Serializable
data class SystemCategoryDto(
    val id: Int,
    val name: String,
    val children: List<SystemCategoryDto> = emptyList()
) {
    fun toDomain(): SystemCategory = SystemCategory(
        id = id,
        name = name,
        children = children.map { it.toDomain() }
    )
}
