package com.sample.wanandroidclean.domain.usecase

import androidx.paging.PagingData
import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for getting paginated articles for a specific project chapter.
 * Updated to support Paging 3 and match the new repository interface.
 */
class GetProjectArticlesUseCase(private val repository: ProjectRepository) {

    /**
     * Returns a flow of paging data for projects in a specific category.
     */
    operator fun invoke(cid: Int): Flow<PagingData<Article>> = 
        repository.getProjectArticlesPaging(cid)
}
