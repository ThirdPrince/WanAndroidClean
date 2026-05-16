package com.sample.wanandroidclean.feature.wxarticle

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.sample.wanandroidclean.domain.entity.Article
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WxArticleScreen(
    onArticleClick: (Article) -> Unit,
    viewModel: WxArticleViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { uiState.chapters.size })
    val scope = rememberCoroutineScope()

    if (uiState.isLoading && uiState.chapters.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            if (uiState.chapters.isNotEmpty()) {
                ScrollableTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    edgePadding = 0.dp,
                    divider = {}
                ) {
                    uiState.chapters.forEachIndexed { index, chapter ->
                        Tab(
                            modifier = Modifier.height(48.dp),
                            selected = pagerState.currentPage == index,
                            onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                            text = { Text(text = chapter.name) }
                        )
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f)
                ) { pageIndex ->
                    val chapterId = uiState.chapters[pageIndex].id
                    
                    // 显式声明类型，辅助 IDE 识别 collectAsLazyPagingItems
                    val pagingItems: LazyPagingItems<Article> = viewModel.getArticlesPaging(chapterId).collectAsLazyPagingItems()

                    ArticlesList(
                        pagingItems = pagingItems,
                        onArticleClick = onArticleClick
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticlesList(
    pagingItems: LazyPagingItems<Article>,
    onArticleClick: (Article) -> Unit,
    modifier: Modifier = Modifier
) {
    val isRefreshing = pagingItems.loadState.refresh is LoadState.Loading

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { pagingItems.refresh() },
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp)) {
            // 使用 Paging 3 标准 items 扩展
            items(
                count = pagingItems.itemCount,
                key = pagingItems.itemKey { it.id },
                contentType = pagingItems.itemContentType { "wx_article" }
            ) { index ->
                val article = pagingItems[index]
                if (article != null) {
                    ArticleItem(
                        article = article,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onArticleClick(article) }
                    )
                }
            }

            // 处理加载更多状态
            renderLoadState(pagingItems)
        }
    }
}

private fun LazyListScope.renderLoadState(pagingItems: LazyPagingItems<Article>) {
    pagingItems.apply {
        when {
            // 加载更多中
            loadState.append is LoadState.Loading -> {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
            }
            // 加载失败 (包括初始刷新和加载更多)
            loadState.refresh is LoadState.Error || loadState.append is LoadState.Error -> {
                val e = if (loadState.refresh is LoadState.Error) loadState.refresh as LoadState.Error else loadState.append as LoadState.Error
                item {
                    ErrorMessage(
                        message = e.error.localizedMessage ?: "网络错误",
                        onClickRetry = { retry() }
                    )
                }
            }
        }
    }
}

@Composable
fun ErrorMessage(message: String, onClickRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        TextButton(onClick = onClickRetry) {
            Text("点击重试")
        }
    }
}

@Composable
fun ArticleItem(article: Article, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.padding(vertical = 5.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (article.isTop) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "置顶",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                val authorText = article.author.ifEmpty { article.shareUser }
                Text(
                    text = "作者: $authorText",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
