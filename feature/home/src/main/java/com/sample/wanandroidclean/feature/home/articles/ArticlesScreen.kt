package com.sample.wanandroidclean.feature.home.articles

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp)
        ) {
            if (uiState.banners.isNotEmpty()) {
                item {
                    BannerPager(
                        banners = uiState.banners,
                        onBannerClick = { banner ->
                            // 将 Banner 包装成 Article 对象以复用详情页跳转逻辑
                            val dummyArticle = Article(
                                id = banner.id,
                                title = banner.title,
                                author = "",
                                shareUser = "",
                                link = banner.url,
                                isTop = false
                            )
                            onArticleClick(dummyArticle)
                        }
                    )
                }
            }

            items(uiState.articles) { article ->
                ArticleItem(
                    article = article,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onArticleClick(article) }
                )
            }
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
                } catch (e: Exception) {
                    // Handle potential cancellation during animation
                }
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
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onBannerClick(banner) } // 为每一张图片添加点击事件
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
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = article.title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (article.isTop) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Top",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                val authorText = article.author.ifEmpty { article.shareUser }
                Text(
                    text = "Author: $authorText",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
