package com.sample.wanandroidclean.data.model

import com.sample.wanandroidclean.data.remote.decodeHtml
import com.sample.wanandroidclean.domain.entity.Article
import kotlinx.serialization.Serializable

/**
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
        title = title.decodeHtml(), // 处理 HTML 转义字符
        author = author.decodeHtml(), // 作者名有时也包含转义
        shareUser = shareUser.decodeHtml(),
        link = link,
        isTop = isTop
    )
}
