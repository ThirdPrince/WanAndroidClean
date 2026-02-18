package com.sample.wanandroidclean.domain.usecase

import com.sample.wanandroidclean.domain.entity.WxChapter
import com.sample.wanandroidclean.domain.repository.WxArticleRepository

/**
 * Use case for getting the list of WeChat article chapters.
 */
class GetWxChaptersUseCase(private val repository: WxArticleRepository) {

    suspend operator fun invoke(): Result<List<WxChapter>> = repository.getWxChapters()
}
