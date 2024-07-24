package com.example.classschedule.ui.screen

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.classschedule.ui.map.MainMapScreen

@Composable
fun MapScreen(mainNavController: NavHostController, openDrawer: () -> Unit) {
    MainMapScreen(mainNavController = mainNavController, openDrawer = openDrawer)
}