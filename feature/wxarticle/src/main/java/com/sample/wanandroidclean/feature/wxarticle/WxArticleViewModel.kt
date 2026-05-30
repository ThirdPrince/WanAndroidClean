package com.sample.wanandroidclean.feature.wxarticle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.entity.WxChapter
import com.sample.wanandroidclean.domain.usecase.GetWxArticlesPagingUseCase
import com.sample.wanandroidclean.domain.usecase.GetWxChaptersUseCase
import kotlinx.coroutines.flow.*

data class WxArticleUiState(
    val isLoading: Boolean = false,
    val chapters: List<WxChapter> = emptyList(),
    val error: String? = null
)

class WxArticleViewModel(
    private val getWxChaptersUseCase: GetWxChaptersUseCase,
    private val getWxArticlesPagingUseCase: GetWxArticlesPagingUseCase
) : ViewModel() {

    /**
     * 响应式公众号章节状态流。
     * 直接观察 UseCase 返回的 Flow (包含数据库缓存 + 网络刷新结果)。
     */
    val uiState: StateFlow<WxArticleUiState> = getWxChaptersUseCase()
        .map { result ->
            result.fold(
                onSuccess = { chapters ->
                    WxArticleUiState(chapters = chapters, isLoading = false)
                },
                onFailure = { e ->
                    WxArticleUiState(error = e.localizedMessage, isLoading = false)
                }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = WxArticleUiState(isLoading = true)
        )

    // 用于缓存不同 chapterId 的分页流，避免重复创建
    private val pagingDataFlowMap = mutableMapOf<Int, Flow<PagingData<Article>>>()

    /**
     * 根据章节 ID 获取分页数据流。
     * 使用 cachedIn 确保在 Pager 切换时状态不丢失。
     */
    fun getArticlesPaging(chapterId: Int): Flow<PagingData<Article>> {
        return pagingDataFlowMap.getOrPut(chapterId) {
            getWxArticlesPagingUseCase(chapterId)
                .cachedIn(viewModelScope)
        }
    }
}
