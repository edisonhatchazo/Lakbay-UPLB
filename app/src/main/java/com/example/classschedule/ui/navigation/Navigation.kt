package com.example.classschedule.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
        composable(Screen.Map.route) { MapScreen(navController) }
    }
}