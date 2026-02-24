package com.sample.wanandroidclean.domain.usecase

import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.repository.ProjectRepository

/**
 * Use case for getting the list of articles for a specific project chapter.
 */
class GetProjectArticlesUseCase(private val repository: ProjectRepository) {

    suspend operator fun invoke(page: Int, cid: Int): Result<List<Article>> = repository.getProjectArticles(page, cid)
}
