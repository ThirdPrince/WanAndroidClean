package com.sample.wanandroidclean.feature.project

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.sample.wanandroidclean.domain.entity.Article
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProjectScreen(viewModel: ProjectViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { uiState.chapters.size })
    val scope = rememberCoroutineScope()

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            if (uiState.chapters.isNotEmpty()) {
                ScrollableTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    edgePadding = 0.dp
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

                HorizontalPager(state = pagerState) { pageIndex ->
                    val chapterId = uiState.chapters[pageIndex].id
                    val articles = uiState.articles[chapterId] ?: emptyList()

                    LaunchedEffect(key1 = chapterId) {
                        if (articles.isEmpty()) {
                            viewModel.loadArticlesForChapter(chapterId)
                        }
                    }

                    ArticlesList(articles = articles)
                }
            }
        }
    }
}

@Composable
fun ArticlesList(articles: List<Article>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(articles) { article ->
            ArticleItem(article = article, modifier = Modifier.fillMaxWidth())
            Divider(color = Color.LightGray, thickness = 0.5.dp)
        }
    }
}

@Composable
fun ArticleItem(article: Article, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .padding(vertical = 8.dp),
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
