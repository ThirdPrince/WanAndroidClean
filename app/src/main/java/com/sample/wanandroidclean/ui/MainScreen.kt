package com.sample.wanandroidclean.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sample.wanandroidclean.feature.home.articles.ArticlesScreen

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
            composable(NavigationItem.System.route) { /* TODO: System Screen */ }
            composable(NavigationItem.Project.route) { /* TODO: Project Screen */ }
            composable(NavigationItem.Collection.route) { /* TODO: Collection Screen */ }
        }
    }
}
