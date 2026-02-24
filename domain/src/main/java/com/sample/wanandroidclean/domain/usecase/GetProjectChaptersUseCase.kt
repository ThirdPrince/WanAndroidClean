package com.sample.wanandroidclean.domain.usecase

import com.sample.wanandroidclean.domain.entity.ProjectChapter
import com.sample.wanandroidclean.domain.repository.ProjectRepository

/**
 * Use case for getting the list of project chapters.
 */
class GetProjectChaptersUseCase(private val repository: ProjectRepository) {

    suspend operator fun invoke(): Result<List<ProjectChapter>> = repository.getProjectChapters()
}
