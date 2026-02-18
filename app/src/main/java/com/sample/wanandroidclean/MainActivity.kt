package com.sample.wanandroidclean

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.sample.wanandroidclean.feature.home.ui.theme.WanAndroidCleanTheme
import com.sample.wanandroidclean.ui.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            WanAndroidCleanTheme {
                MainScreen()
            }
        }
    }
}
