package com.sample.wanandroidclean.domain.usecase

import com.sample.wanandroidclean.domain.entity.ProjectChapter
import com.sample.wanandroidclean.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for getting the list of project chapters.
 * Updated to support the reactive flow from ProjectRepository.
 */
class GetProjectChaptersUseCase(private val repository: ProjectRepository) {

    /**
     * Returns a flow of project chapter results, supporting offline-first logic.
     */
    operator fun invoke(): Flow<Result<List<ProjectChapter>>> = repository.getProjectChapters()
}
