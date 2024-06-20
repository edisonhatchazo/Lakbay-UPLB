package com.example.classschedule.ui.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Home : Screen("home", "Home", Icons.Default.Home)
    data object Classes : Screen("classes", "Classes", Icons.Default.List)
    data object Building : Screen("buildings", "Buildings", Icons.Default.Build)
    data object Map : Screen("map", "Map", Icons.Default.LocationOn)
}