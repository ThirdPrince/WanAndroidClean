package com.sample.wanandroidclean.data.model

import com.sample.wanandroidclean.domain.entity.Article
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@InternalSerializationApi /**
 * Data Transfer Object for articles, compatible with both normal and collection lists.
 */
@Serializable
data class ArticleDto(
    val id: Int = 0,
    val originId: Int = 0, // Used in collection list to refer to the original article id
    val title: String = "",
    val author: String = "",
    val shareUser: String = "",
    val link: String = ""
) {
    fun toDomain(isTop: Boolean = false): Article = Article(
        id = if (originId != 0) originId else id,
        title = title,
        author = author,
        shareUser = shareUser,
        link = link,
        isTop = isTop
    )
}
