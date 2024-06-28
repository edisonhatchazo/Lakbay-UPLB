package com.example.classschedule.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.classschedule.ui.screen.BuildingScreen
import com.example.classschedule.ui.screen.ClassesScreen
import com.example.classschedule.ui.screen.HomeScreen
import com.example.classschedule.ui.screen.MapScreen
import com.example.classschedule.ui.screen.Screen

@Composable
fun Navigation(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController, startDestination = Screen.Home.route, modifier = modifier) {
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.Classes.route) { ClassesScreen(navController) }
        composable(Screen.Building.route) { BuildingScreen(navController) }
        composable(Screen.Map.route) { MapScreen("UPLB Gate", 14.16747822735461, 121.24338486047947) }

        composable(
            route = "map_screen/{title}/{latitude}/{longitude}",
            arguments = listOf(
                navArgument("title") { type = NavType.StringType },
                navArgument("latitude") { type = NavType.StringType},
                navArgument("longitude") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title") ?: ""
            val latitude = backStackEntry.arguments?.getString("latitude")?.toDoubleOrNull() ?: 0.0
            val longitude = backStackEntry.arguments?.getString("longitude")?.toDoubleOrNull() ?: 0.0
            MapScreen(title, latitude, longitude)
        }
    }
}