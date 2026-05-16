package com.sample.wanandroidclean.domain.repository

import androidx.paging.PagingData
import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.entity.WxChapter
import kotlinx.coroutines.flow.Flow

/**
 * Interface for the WeChat article repository.
 */
interface WxArticleRepository {

    /**
     * Fetches the list of WeChat article chapters.
     */
    suspend fun getWxChapters(): Result<List<WxChapter>>

    /**
     * Returns a flow of paging data for a specific WeChat article chapter.
     */
    fun getWxArticlesPaging(chapterId: Int): Flow<PagingData<Article>>
}
