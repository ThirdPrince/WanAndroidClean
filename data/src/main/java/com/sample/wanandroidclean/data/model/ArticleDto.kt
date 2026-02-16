package com.sample.wanandroidclean.data.model

import com.sample.wanandroidclean.domain.entity.Article
import kotlinx.serialization.Serializable

@Serializable
data class ArticleDto(
    val id: Int,
    val title: String,
    val author: String,
    val shareUser: String,
    val link: String
) {
    fun toDomain(): Article = Article(
        id = id,
        title = title,
        author = author,
        shareUser = shareUser,
        link = link
    )
}
