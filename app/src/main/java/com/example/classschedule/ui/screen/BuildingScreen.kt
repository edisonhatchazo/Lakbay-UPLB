package com.example.classschedule.ui.screen

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.classschedule.ui.buildingScreens.BuildingApp

@Composable
fun BuildingScreen(mainNavController: NavHostController) {
    BuildingApp(mainNavController)
}