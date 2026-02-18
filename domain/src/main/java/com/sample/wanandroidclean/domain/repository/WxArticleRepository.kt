package com.sample.wanandroidclean.domain.repository

import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.entity.WxChapter

/**
 * Interface for the WeChat article repository.
 */
interface WxArticleRepository {

    /**
     * Fetches the list of WeChat article chapters.
     */
    suspend fun getWxChapters(): Result<List<WxChapter>>

    /**
     * Fetches the list of articles for a specific WeChat article chapter.
     */
    suspend fun getWxArticles(chapterId: Int, page: Int): Result<List<Article>>
}
