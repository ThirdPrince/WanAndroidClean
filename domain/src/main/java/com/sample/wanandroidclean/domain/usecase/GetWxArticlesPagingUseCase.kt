package com.sample.wanandroidclean.domain.usecase

import androidx.paging.PagingData
import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.repository.WxArticleRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for getting paginated articles for a specific WeChat account.
 */
class GetWxArticlesPagingUseCase(private val repository: WxArticleRepository) {
    operator fun invoke(chapterId: Int): Flow<PagingData<Article>> =
        repository.getWxArticlesPaging(chapterId)
}
