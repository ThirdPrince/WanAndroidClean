package com.sample.wanandroidclean.domain.usecase

import com.sample.wanandroidclean.domain.repository.ArticleRepository
import com.sample.wanandroidclean.domain.repository.CollectionRepository

/**
 * 收藏逻辑：实现了网络请求成功后再静默更新本地数据库，
 * 配合 UI 层的局部状态拦截，实现即时反馈且无列表闪烁。
 */
class ToggleCollectUseCase(
    private val collectionRepository: CollectionRepository,
    private val articleRepository: ArticleRepository
) {
    suspend operator fun invoke(id: Int, collect: Boolean): Result<Unit> {
        // 1. 发起网络请求
        val result = if (collect) {
            collectionRepository.collect(id)
        } else {
            collectionRepository.uncollect(id)
        }

        // 2. 只有在网络成功后，才静默更新数据库
        // 这样做可以避免在点击瞬间因数据库变化导致 Paging 列表产生跳动（闪烁）
        if (result.isSuccess) {
            articleRepository.updateLocalCollectStatus(id, collect)
        }

        return result
    }
}
