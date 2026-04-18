package com.sample.wanandroidclean.feature.system

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedSuggestionChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.domain.entity.Navigation
import org.koin.androidx.compose.koinViewModel

@Composable
fun NavigationScreen(
    onArticleClick: (Article) -> Unit,
    viewModel: SystemViewModel = koinViewModel()
) {
    val uiState by viewModel.navigationUiState.collectAsStateWithLifecycle()

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(uiState.navigation) {
                NavigationItemContent(
                    category = it, 
                    onArticleClick = onArticleClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NavigationItemContent(
    category: Navigation, 
    onArticleClick: (Article) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.padding(16.dp)) {
        Text(text = category.name, style = MaterialTheme.typography.titleLarge)
        FlowRow(modifier = Modifier.padding(top = 8.dp)) {
            category.articles.forEach { article ->
                ElevatedSuggestionChip(
                    onClick = { onArticleClick(article) },
                    label = { Text(article.title) },
                    modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
                )
            }
        }
    }
}
