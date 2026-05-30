package com.sample.wanandroidclean.domain.usecase

import com.sample.wanandroidclean.domain.entity.WxChapter
import com.sample.wanandroidclean.domain.repository.WxArticleRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for getting the reactive flow of WeChat article chapters.
 */
class GetWxChaptersUseCase(private val repository: WxArticleRepository) {

    /**
     * Returns a flow of chapter results, supporting offline-first logic.
     */
    operator fun invoke(): Flow<Result<List<WxChapter>>> = repository.getWxChapters()
}
