package com.sample.wanandroidclean.data.local.entity

import androidx.room.Entity
import com.sample.wanandroidclean.domain.entity.Article

@Entity(
    tableName = "articles",
    primaryKeys = ["id", "categoryId"]
)
data class ArticleEntity(
    val id: Int,
    val categoryId: Int, 
    val title: String,
    val author: String,
    val shareUser: String,
    val link: String,
    val isTop: Boolean,
    val collect: Boolean, // 新增：持久化收藏状态
    val page: Int,
    val orderInPage: Int
) {
    fun toDomain(): Article = Article(
        id = id,
        title = title,
        author = author,
        shareUser = shareUser,
        link = link,
        isTop = isTop,
        collect = collect
    )

    companion object {
        fun fromDomain(article: Article, categoryId: Int, page: Int, orderInPage: Int): ArticleEntity = ArticleEntity(
            id = article.id,
            categoryId = categoryId,
            title = article.title,
            author = article.author,
            shareUser = article.shareUser,
            link = article.link,
            isTop = article.isTop,
            collect = article.collect,
            page = page,
            orderInPage = orderInPage
        )
    }
}
