package com.sample.wanandroidclean

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sample.wanandroidclean.presentation.articles.ArticlesScreen
import com.sample.wanandroidclean.presentation.ui.theme.WanAndroidCleanTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WanAndroidCleanTheme {
                ArticlesScreen()
            }
        }
    }
}
