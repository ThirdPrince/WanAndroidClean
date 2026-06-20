package com.sample.wanandroidclean.domain.repository

import androidx.paging.PagingData
import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.entity.ProjectChapter
import kotlinx.coroutines.flow.Flow

/**
 * Interface for the project repository.
 */
interface ProjectRepository {

    /**
     * Returns a flow of project chapters, supporting offline-first logic.
     */
    fun getProjectChapters(): Flow<Result<List<ProjectChapter>>>

    /**
     * Returns a flow of paging data for a specific project chapter.
     */
    fun getProjectArticlesPaging(chapterId: Int): Flow<PagingData<Article>>
}
