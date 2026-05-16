package com.sample.wanandroidclean.domain.usecase

import androidx.paging.PagingData
import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.repository.WxArticleRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for getting paginated articles for a specific WeChat article chapter.
 * Updated to support Paging 3 and match the new repository interface.
 */
class GetWxArticlesUseCase(private val repository: WxArticleRepository) {

    /**
     * Returns a flow of paging data for articles in a specific chapter.
     */
    operator fun invoke(chapterId: Int): Flow<PagingData<Article>> = 
        repository.getWxArticlesPaging(chapterId)
}
