package com.sample.wanandroidclean.domain.repository

import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.entity.ProjectChapter

/**
 * Interface for the project repository.
 */
interface ProjectRepository {

    /**
     * Fetches the list of project chapters.
     */
    suspend fun getProjectChapters(): Result<List<ProjectChapter>>

    /**
     * Fetches the list of articles for a specific project chapter.
     */
    suspend fun getProjectArticles(page: Int, cid: Int): Result<List<Article>>
}
