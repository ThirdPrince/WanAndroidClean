package com.sample.wanandroidclean.feature.web

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebScreen(
    title: String,
    url: String,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = title, style = MaterialTheme.typography.titleMedium, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        AndroidView(
            modifier = Modifier.padding(innerPadding),
            factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    
                    // 核心优化配置
                    settings.apply {
                        javaScriptEnabled = true // 开启 JS
                        domStorageEnabled = true // 开启 DOM 存储 (微信文章必需)
                        databaseEnabled = true
                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW // 允许 HTTPS 页面加载 HTTP 资源
                        useWideViewPort = true // 支持 viewport 标签
                        loadWithOverviewMode = true // 缩放至屏幕大小
                        setSupportZoom(true) // 支持缩放
                        builtInZoomControls = true // 显示缩放控件
                        displayZoomControls = false // 隐藏缩放按钮
                        cacheMode = WebSettings.LOAD_DEFAULT // 使用默认缓存模式
                    }

                    webViewClient = WebViewClient()
                    loadUrl(url)
                }
            },
            update = { webView ->
                // 仅当 URL 真正改变时才重新加载，避免 Compose 重组导致的重复刷新
                if (webView.url != url) {
                    webView.loadUrl(url)
                }
            }
        )
    }
}
