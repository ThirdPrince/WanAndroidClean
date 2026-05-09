package com.sample.wanandroidclean.feature.web

import android.annotation.SuppressLint
import android.content.Intent
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    // 1. 持有 WebView 实例引用
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    
    // 2. 追踪是否可以回退的状态
    var canGoBackState by remember { mutableStateOf(false) }

    // 3. 处理物理返回键
    BackHandler(enabled = canGoBackState) {
        webViewInstance?.goBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = title, style = MaterialTheme.typography.titleMedium, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (canGoBackState) {
                            webViewInstance?.goBack()
                        } else {
                            onBackClick()
                        }
                    }) {
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
                    
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        databaseEnabled = true
                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        useWideViewPort = true
                        loadWithOverviewMode = true
                    }

                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            // 页面加载完成后同步状态
                            canGoBackState = view?.canGoBack() ?: false
                        }

                        override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                            super.doUpdateVisitedHistory(view, url, isReload)
                            // 网页内部跳转时同步状态
                            canGoBackState = view?.canGoBack() ?: false
                        }

                        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                            val uri = request?.url ?: return false
                            val urlString = uri.toString()

                            // 标准网页链接
                            if (urlString.startsWith("http://") || urlString.startsWith("https://")) {
                                return false
                            }

                            // 处理 intent://
                            if (urlString.startsWith("intent://")) {
                                try {
                                    val intent = Intent.parseUri(urlString, Intent.URI_INTENT_SCHEME)
                                    val info = context.packageManager.resolveActivity(intent, 0)
                                    if (info != null) {
                                        context.startActivity(intent)
                                    } else {
                                        intent.getStringExtra("browser_fallback_url")?.let { fallback ->
                                            view?.loadUrl(fallback)
                                        }
                                    }
                                    return true
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }

                            // 处理其他协议
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, uri)
                                context.startActivity(intent)
                                return true
                            } catch (e: Exception) {
                                return true
                            }
                        }
                    }
                    
                    loadUrl(url)
                    webViewInstance = this
                }
            },
            update = { 
                if (it.url == null) {
                    it.loadUrl(url)
                }
            }
        )
    }
}
