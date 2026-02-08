package com.sample.wanandroidclean.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(val route: String, val icon: ImageVector, val title: String) {
    object Home : NavigationItem("home", Icons.Filled.Home, "Home")
    object System : NavigationItem("system", Icons.Filled.Person, "System")
    object Project : NavigationItem("project", Icons.Filled.Favorite, "Project")
    object Collection : NavigationItem("collection", Icons.Filled.Favorite, "Collection")
}
