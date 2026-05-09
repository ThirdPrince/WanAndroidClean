package com.sample.wanandroidclean.feature.home.articles

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.entity.Banner
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun ArticlesScreen(
    onArticleClick: (Article) -> Unit,
    viewModel: ArticlesViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // 1. 使用显式类型声明辅助 IDE 识别扩展函数，解决 collectAsLazyPagingItems 报错
    val pagingItems: LazyPagingItems<Article> = viewModel.articlesPagingData.collectAsLazyPagingItems()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp)
    ) {
        // Banner 部分
        if (uiState.banners.isNotEmpty()) {
            item {
                BannerPager(
                    banners = uiState.banners,
                    onBannerClick = { banner ->
                        val dummyArticle = Article(
                            id = banner.id,
                            title = banner.title,
                            author = "",
                            shareUser = "",
                            link = banner.url
                        )
                        onArticleClick(dummyArticle)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // 2. 渲染分页文章列表
        items(
            count = pagingItems.itemCount,
            key = pagingItems.itemKey { it.id },
            contentType = pagingItems.itemContentType { "article" }
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

        // 3. 处理分页加载状态
        renderPagingState(pagingItems)
    }
}

/**
 * 将加载状态提取为扩展函数，增强代码可读性
 */
private fun LazyListScope.renderPagingState(pagingItems: LazyPagingItems<Article>) {
    pagingItems.apply {
        when {
            // 初始加载中 (且本地暂无缓存)
            loadState.refresh is LoadState.Loading && itemCount == 0 -> {
                item {
                    Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
            // 加载更多中
            loadState.append is LoadState.Loading -> {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
            }
            // 刷新失败
            loadState.refresh is LoadState.Error -> {
                val error = loadState.refresh as LoadState.Error
                item {
                    ErrorMessage(
                        message = error.error.localizedMessage ?: "Refresh failed",
                        onClickRetry = { retry() }
                    )
                }
            }
            // 加载更多失败
            loadState.append is LoadState.Error -> {
                val error = loadState.append as LoadState.Error
                item {
                    ErrorMessage(
                        message = error.error.localizedMessage ?: "Load more failed",
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
        Text(text = message, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onClickRetry) {
            Text("Retry")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BannerPager(
    banners: List<Banner>,
    onBannerClick: (Banner) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { banners.size })
    var isDragged by remember { mutableStateOf(false) }

    LaunchedEffect(isDragged) {
        if (!isDragged) {
            while (true) {
                delay(3000)
                try {
                    pagerState.animateScrollToPage((pagerState.currentPage + 1) % pagerState.pageCount)
                } catch (e: Exception) {}
            }
        }
    }

    Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
        HorizontalPager(state = pagerState) { index ->
            val banner = banners[index]
            AsyncImage(
                model = banner.imagePath,
                contentDescription = banner.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().clickable { onBannerClick(banner) }
            )
        }
        val isDraggedState = pagerState.interactionSource.collectIsDraggedAsState()
        isDragged = isDraggedState.value
    }
}

@Composable
fun ArticleItem(article: Article, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.padding(vertical = 5.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = article.title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (article.isTop) {
                    Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(MaterialTheme.colorScheme.primary).padding(horizontal = 6.dp, vertical = 2.dp)) {
                        Text(text = "Top", color = MaterialTheme.colorScheme.onPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(text = "Author: ${article.author.ifEmpty { article.shareUser }}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
