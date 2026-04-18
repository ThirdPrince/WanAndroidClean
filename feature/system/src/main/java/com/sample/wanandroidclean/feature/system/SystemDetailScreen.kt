package com.sample.wanandroidclean.feature.system

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.entity.SystemCategory
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SystemDetailScreen(
    category: SystemCategory,
    onBackClick: () -> Unit,
    onArticleClick: (Article) -> Unit,
    viewModel: SystemDetailViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val children = category.children
    val pagerState = rememberPagerState(pageCount = { children.size })
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = category.name) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (children.isNotEmpty()) {
                ScrollableTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    edgePadding = 0.dp,
                    divider = {}
                ) {
                    children.forEachIndexed { index, child ->
                        Tab(
                            modifier = Modifier.height(48.dp),
                            selected = pagerState.currentPage == index,
                            onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                            text = { Text(text = child.name) }
                        )
                    }
                }

                HorizontalPager(state = pagerState) { pageIndex ->
                    val child = children[pageIndex]
                    val articles = uiState.articles[child.id] ?: emptyList()
                    val isLoading = uiState.loadingCids.contains(child.id)

                    LaunchedEffect(key1 = child.id) {
                        if (articles.isEmpty()) {
                            viewModel.loadArticlesForCategory(child.id)
                        }
                    }

                    if (isLoading && articles.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else {
                        SystemArticleList(
                            articles = articles,
                            onArticleClick = onArticleClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SystemArticleList(
    articles: List<Article>,
    onArticleClick: (Article) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(articles) { article ->
            SystemArticleItem(article = article, onClick = { onArticleClick(article) })
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }
    }
}

@Composable
fun SystemArticleItem(article: Article, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        color = Color.Transparent
    ) {
        Column {
            Text(
                text = article.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Text(
                    text = "作者: ${article.author.ifEmpty { article.shareUser }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
