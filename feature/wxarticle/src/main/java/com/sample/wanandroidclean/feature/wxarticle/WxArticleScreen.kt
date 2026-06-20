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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    onLoginClick: () -> Unit,
    viewModel: WxArticleViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val chapters = uiState.chapters
    val pagerState = rememberPagerState(pageCount = { chapters.size })
    val scope = rememberCoroutineScope()

    if (uiState.isLoading && chapters.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            if (chapters.isNotEmpty()) {
                ScrollableTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    edgePadding = 0.dp,
                    divider = {}
                ) {
                    chapters.forEachIndexed { index, chapter ->
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
                    val chapterId = chapters[pageIndex].id
                    val pagingItems: LazyPagingItems<Article> = viewModel.getArticlesPaging(chapterId).collectAsLazyPagingItems()

                    ArticlesList(
                        pagingItems = pagingItems,
                        onArticleClick = onArticleClick,
                        onCollectClick = { article ->
                            // 判断登录状态
                            if (isLoggedIn) {
                                viewModel.toggleCollect(article)
                            } else {
                                onLoginClick()
                            }
                        }
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
    onCollectClick: (Article) -> Unit,
    modifier: Modifier = Modifier
) {
    val isRefreshing = pagingItems.loadState.refresh is LoadState.Loading

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { pagingItems.refresh() },
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp)
        ) {
            items(
                count = pagingItems.itemCount,
                key = pagingItems.itemKey { it.id },
                contentType = pagingItems.itemContentType { "wx_article" }
            ) { index ->
                val article = pagingItems[index]
                if (article != null) {
                    ArticleItem(
                        article = article,
                        onArticleClick = { onArticleClick(article) },
                        onCollectClick = { onCollectClick(article) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            renderLoadState(pagingItems)
        }
    }
}

private fun LazyListScope.renderLoadState(pagingItems: LazyPagingItems<Article>) {
    pagingItems.apply {
        when {
            loadState.append is LoadState.Loading -> {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
            }
            loadState.refresh is LoadState.Error || loadState.append is LoadState.Error -> {
                val e = if (loadState.refresh is LoadState.Error) loadState.refresh as LoadState.Error else loadState.append as LoadState.Error
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = e.error.localizedMessage ?: "网络错误", color = MaterialTheme.colorScheme.error)
                        TextButton(onClick = { retry() }) {
                            Text("点击重试")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ArticleItem(
    article: Article,
    onArticleClick: () -> Unit,
    onCollectClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(vertical = 5.dp)
            .clickable(onClick = onArticleClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (article.isTop) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = "置顶", color = MaterialTheme.colorScheme.onPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(text = article.title, style = MaterialTheme.typography.titleMedium)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "作者: ${article.author.ifEmpty { article.shareUser }}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            IconButton(onClick = onCollectClick) {
                Icon(
                    imageVector = if (article.collect) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Collect",
                    tint = if (article.collect) Color.Red else MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
