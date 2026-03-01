package com.sample.wanandroidclean.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sample.wanandroidclean.feature.home.articles.ArticlesScreen
import com.sample.wanandroidclean.feature.mine.LoginScreen
import com.sample.wanandroidclean.feature.mine.MineScreen
import com.sample.wanandroidclean.feature.project.ProjectScreen
import com.sample.wanandroidclean.feature.system.SystemScreen
import com.sample.wanandroidclean.feature.web.WebScreen
import com.sample.wanandroidclean.feature.wxarticle.WxArticleScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        NavigationItem.Home.route,
        NavigationItem.System.route,
        NavigationItem.WxArticle.route,
        NavigationItem.Project.route,
        NavigationItem.Mine.route
    )

    Scaffold(
        bottomBar = {
            // 使用动画控制导航栏的显示和隐藏
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        // 使用 Box 配合 padding 确保布局平稳
        Box(modifier = Modifier.padding(if (showBottomBar) innerPadding else androidx.compose.foundation.layout.PaddingValues())) {
            NavHost(
                navController = navController,
                startDestination = NavigationItem.Home.route,
                // 配置全局的进入和退出动画
                enterTransition = {
                    slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) + fadeIn()
                },
                exitTransition = {
                    slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300)) + fadeOut()
                },
                popEnterTransition = {
                    slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300)) + fadeIn()
                },
                popExitTransition = {
                    slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) + fadeOut()
                }
            ) {
                composable(NavigationItem.Home.route) {
                    ArticlesScreen(onArticleClick = { article ->
                        val encodedUrl = URLEncoder.encode(article.link, StandardCharsets.UTF_8.toString())
                        navController.navigate("article_detail/${article.title}/$encodedUrl")
                    })
                }
                composable(NavigationItem.System.route) { SystemScreen() }
                composable(NavigationItem.WxArticle.route) {
                    WxArticleScreen(onArticleClick = { article ->
                        val encodedUrl = URLEncoder.encode(article.link, StandardCharsets.UTF_8.toString())
                        navController.navigate("article_detail/${article.title}/$encodedUrl")
                    })
                }
                composable(NavigationItem.Project.route) {
                    ProjectScreen(onArticleClick = { article ->
                        val encodedUrl = URLEncoder.encode(article.link, StandardCharsets.UTF_8.toString())
                        navController.navigate("article_detail/${article.title}/$encodedUrl")
                    })
                }
                composable(NavigationItem.Mine.route) {
                    MineScreen(onLoginClick = { navController.navigate("login") })
                }

                composable("login") {
                    LoginScreen(onBackClick = { navController.popBackStack() })
                }

                composable(
                    route = "article_detail/{title}/{url}",
                    arguments = listOf(
                        navArgument("title") { type = NavType.StringType },
                        navArgument("url") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val title = backStackEntry.arguments?.getString("title") ?: ""
                    val encodedUrl = backStackEntry.arguments?.getString("url") ?: ""
                    val url = URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8.toString())
                    WebScreen(
                        title = title,
                        url = url,
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
