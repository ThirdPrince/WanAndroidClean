package com.sample.wanandroidclean.data.model

import com.sample.wanandroidclean.domain.entity.Navigation
import kotlinx.serialization.Serializable

@Serializable
data class NavigationDto(
    val cid: Int,
    val name: String,
    val articles: List<ArticleDto>
) {
    fun toDomain(): Navigation = Navigation(
        id = cid,
        name = name,
        articles = articles.map { it.toDomain() }
    )
}
