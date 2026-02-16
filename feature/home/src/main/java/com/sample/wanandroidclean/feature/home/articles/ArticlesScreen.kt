package com.sample.wanandroidclean.feature.home.articles

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sample.wanandroidclean.domain.entity.Article
import com.sample.wanandroidclean.feature.home.articles.ArticlesViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ArticlesScreen(viewModel: ArticlesViewModel = koinViewModel()) {
    val articles by viewModel.articles.collectAsState()
    ArticlesList(articles = articles, modifier = Modifier.fillMaxSize())
}

@Composable
fun ArticlesList(articles: List<Article>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 8.dp)
    ) {
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
            
            val authorText = article.author.ifEmpty { article.shareUser }
            Text(
                text = "$authorText",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
