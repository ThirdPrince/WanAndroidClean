package com.sample.wanandroidclean.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sample.wanandroidclean.feature.home.articles.ArticlesScreen
import com.sample.wanandroidclean.feature.mine.LoginScreen
import com.sample.wanandroidclean.feature.mine.MineScreen
import com.sample.wanandroidclean.feature.project.ProjectScreen
import com.sample.wanandroidclean.feature.system.SystemScreen
import com.sample.wanandroidclean.feature.wxarticle.WxArticleScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavigationItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavigationItem.Home.route) { ArticlesScreen() }
            composable(NavigationItem.System.route) { SystemCategoryListWrapper() }
            composable(NavigationItem.WxArticle.route) { WxArticleScreen() }
            composable(NavigationItem.Project.route) { ProjectScreen() }
            composable(NavigationItem.Mine.route) { 
                MineScreen(onLoginClick = { navController.navigate("login") }) 
            }
            
            composable("login") { 
                LoginScreen(onBackClick = { navController.popBackStack() }) 
            }
        }
    }
}

// A simple wrapper to provide SystemScreen until we have a better way to handle its dependencies
@Composable
fun SystemCategoryListWrapper() {
    SystemScreen()
}
