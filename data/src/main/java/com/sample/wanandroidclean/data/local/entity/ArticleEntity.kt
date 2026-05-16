package com.sample.wanandroidclean.data.local.entity

import androidx.room.Entity
import com.sample.wanandroidclean.domain.entity.Article

/**
 * 增强的文章实体。
 * 使用 (id, categoryId) 作为复合主键，以区分同一篇文章出现在不同模块/分类中的情况。
 * categoryId: 首页=0, 公众号=chapterId, 项目=chapterId
 */
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
    val page: Int,
    val orderInPage: Int
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
        fun fromDomain(article: Article, categoryId: Int, page: Int, orderInPage: Int): ArticleEntity = ArticleEntity(
            id = article.id,
            categoryId = categoryId,
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
