package com.example.classschedule.ui.buildingScreens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.classschedule.ui.navigation.BuildingNavHost

@Composable
fun BuildingApp(mainNavController: NavHostController, navController: NavHostController = rememberNavController()) {
    BuildingNavHost(navController = navController, mainNavController = mainNavController)
}