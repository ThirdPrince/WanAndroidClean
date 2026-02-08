package com.sample.wanandroidclean

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sample.wanandroidclean.feature.home.ui.theme.WanAndroidCleanTheme
import com.sample.wanandroidclean.ui.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WanAndroidCleanTheme {
                MainScreen()
            }
        }
    }
}
