package com.sample.wanandroidclean.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sample.wanandroidclean.domain.entity.Article

@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val author: String,
    val shareUser: String,
    val link: String,
    val isTop: Boolean,
    val page: Int, // Track which page this article belongs to
    val orderInPage: Int // Track order within the page
) {
    fun toDomain(): Article = Article(
        id = id,
        title = title,
        author = author,
        shareUser = shareUser,
        link = link,
        isTop = isTop
    )

    companion object {
        fun fromDomain(article: Article, page: Int, orderInPage: Int): ArticleEntity = ArticleEntity(
            id = article.id,
            title = article.title,
            author = article.author,
            shareUser = article.shareUser,
            link = article.link,
            isTop = article.isTop,
            page = page,
            orderInPage = orderInPage
        )
    }
}
