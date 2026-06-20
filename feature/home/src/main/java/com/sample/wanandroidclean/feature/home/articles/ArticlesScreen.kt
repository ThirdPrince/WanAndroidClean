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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.entity.Banner
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticlesScreen(
    onArticleClick: (Article) -> Unit,
    onLoginClick: () -> Unit, // 新增：跳转登录回调
    viewModel: ArticlesViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagingItems: LazyPagingItems<Article> = viewModel.articlesPagingData.collectAsLazyPagingItems()
    
    val snackbarHostState = remember { SnackbarHostState() }
    val pullToRefreshState = rememberPullToRefreshState()
    
    val isRefreshing = pagingItems.loadState.refresh is LoadState.Loading || uiState.isBannersLoading

    LaunchedEffect(pagingItems.loadState.refresh) {
        val refreshState = pagingItems.loadState.refresh
        if (refreshState is LoadState.Error) {
            snackbarHostState.showSnackbar(
                message = refreshState.error.localizedMessage ?: "刷新失败",
                actionLabel = "重试"
            ).also { result ->
                if (result == SnackbarResult.ActionPerformed) {
                    pagingItems.refresh()
                    viewModel.loadBanners()
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        PullToRefreshBox(
            state = pullToRefreshState,
            isRefreshing = isRefreshing,
            onRefresh = {
                pagingItems.refresh()
                viewModel.loadBanners()
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp)
            ) {
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

                items(
                    count = pagingItems.itemCount,
                    key = pagingItems.itemKey { it.id },
                    contentType = pagingItems.itemContentType { "article" }
                ) { index ->
                    val article = pagingItems[index]
                    if (article != null) {
                        ArticleItem(
                            article = article,
                            onArticleClick = { onArticleClick(article) },
                            onCollectClick = {
                                // 核心逻辑：判断登录状态
                                if (uiState.isLoggedIn) {
                                    viewModel.toggleCollect(article)
                                } else {
                                    onLoginClick()
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                renderAppendState(pagingItems)
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
                Text(text = article.title, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
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
                    Text(
                        text = "作者: ${article.author.ifEmpty { article.shareUser }}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 收藏按钮
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

private fun LazyListScope.renderAppendState(pagingItems: LazyPagingItems<Article>) {
    pagingItems.apply {
        when (loadState.append) {
            is LoadState.Loading -> {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
            }
            is LoadState.Error -> {
                item {
                    ErrorMessage(
                        message = "加载失败，请点击重试",
                        onClickRetry = { retry() }
                    )
                }
            }
            else -> {}
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
            Text("重试")
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
    val context = LocalContext.current

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
            val request = remember(banner.imagePath) {
                ImageRequest.Builder(context)
                    .data(banner.imagePath)
                    .crossfade(true)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .build()
            }
            AsyncImage(
                model = request,
                contentDescription = banner.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onBannerClick(banner) }
            )
        }
        val isDraggedState = pagerState.interactionSource.collectIsDraggedAsState()
        isDragged = isDraggedState.value
    }
}
