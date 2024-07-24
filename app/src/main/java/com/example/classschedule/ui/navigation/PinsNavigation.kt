package com.example.classschedule.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.classschedule.ui.buildingScreens.pins.PinsDetailsDestination
import com.example.classschedule.ui.buildingScreens.pins.PinsDetailsScreen
import com.example.classschedule.ui.buildingScreens.pins.PinsEditDestination
import com.example.classschedule.ui.buildingScreens.pins.PinsEditScreen
import com.example.classschedule.ui.buildingScreens.pins.PinsEntryDestination
import com.example.classschedule.ui.buildingScreens.pins.PinsEntryScreen
import com.example.classschedule.ui.buildingScreens.pins.PinsHomeDestination
import com.example.classschedule.ui.buildingScreens.pins.PinsScreen
import com.example.classschedule.ui.map.GuideMapDestination
import com.example.classschedule.ui.map.GuideMapScreen

@Composable
fun PinsNavHost(
    navController: NavHostController,
    mainNavController: NavHostController,
    modifier: Modifier = Modifier,
    openDrawer: () -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = PinsHomeDestination.route,
        modifier = modifier
    ) {
        composable(route = PinsHomeDestination.route){
            PinsScreen(
                navigateToPinsEntry = { navController.navigate(PinsEntryDestination.route) },
                navigateToPinsUpdate = {
                    navController.navigate("${PinsDetailsDestination.route}/${it}")
                },
                openDrawer = openDrawer
            )
        }
        composable(route = PinsEntryDestination.route){
            PinsEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() })
        }
        composable(
            route = PinsDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(PinsDetailsDestination.PINIDARG){
                type = NavType.IntType
            })
        ){
            PinsDetailsScreen(
                navigateToEditPin = {navController.navigate("${PinsEditDestination.route}/${it}")},
                navigateBack = { navController.navigateUp() },
                mainNavController = mainNavController,
                navigateToMap = {navController.navigate("${GuideMapDestination.route}/${it}")})

        }

        composable(
            route = PinsEditDestination.routeWithArgs,
            arguments = listOf(navArgument(PinsEditDestination.PINSIDARG){
                type = NavType.IntType
            })
        ){
            PinsEditScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() })
        }

        composable(
            route = GuideMapDestination.routeWithArgs,
            arguments = listOf(navArgument(GuideMapDestination.MAPDATAIDARG){
                type = NavType.IntType
            })
        ) {
            GuideMapScreen(
                navigateBack = { navController.navigateUp() }
            )
        }
    }
}