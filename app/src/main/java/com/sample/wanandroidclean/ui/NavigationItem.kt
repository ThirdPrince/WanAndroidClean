package com.sample.wanandroidclean.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(val route: String, val icon: ImageVector, val title: String) {
    object Home : NavigationItem("home", Icons.Filled.Home, "Home")
    object System : NavigationItem("system", Icons.Filled.Build, "System")
    object WxArticle : NavigationItem("wxarticle", Icons.Filled.AccountCircle, "WxArticle")
    object Project : NavigationItem("project", Icons.Filled.Favorite, "Project")
    object Mine : NavigationItem("mine", Icons.Filled.Person, "Mine")
}
