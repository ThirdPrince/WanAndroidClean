package com.sample.wanandroidclean.domain.usecase

import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.repository.WxArticleRepository

/**
 * Use case for getting the list of articles for a specific WeChat article chapter.
 */
class GetWxArticlesUseCase(private val repository: WxArticleRepository) {

    suspend operator fun invoke(chapterId: Int, page: Int): Result<List<Article>> = repository.getWxArticles(chapterId, page)
}
