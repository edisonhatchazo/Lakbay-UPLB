package com.example.classschedule.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.classschedule.ui.building.pins.PinsDetailsDestination
import com.example.classschedule.ui.building.pins.PinsDetailsScreen
import com.example.classschedule.ui.building.pins.PinsEditDestination
import com.example.classschedule.ui.building.pins.PinsEditScreen
import com.example.classschedule.ui.building.pins.PinsEntryDestination
import com.example.classschedule.ui.building.pins.PinsEntryScreen
import com.example.classschedule.ui.building.pins.PinsHomeDestination
import com.example.classschedule.ui.building.pins.PinsScreen
import com.example.classschedule.ui.building.uplb.BuildingHomeDestination
import com.example.classschedule.ui.building.uplb.BuildingHomeScreen

@Composable
fun BuildingNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = BuildingHomeDestination.route,
        modifier = modifier
    ) {
        composable(route = BuildingHomeDestination.route) {
            BuildingHomeScreen(
                navigateToPinsHomeDestination = { navController.navigate(PinsHomeDestination.route) }
            )
        }





        composable(route = PinsHomeDestination.route){
            PinsScreen(
                navigateToPinsEntry = { navController.navigate(PinsEntryDestination.route) },
                navigateToPinsUpdate = {
                    navController.navigate("${PinsDetailsDestination.route}/${it}")
                },
                navigateToBuildingHomeDestination = {navController.navigate(BuildingHomeDestination.route)}
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
                navigateToEditPin = {navController.navigate("${PinsEditDestination.route}/$it")},
                navigateBack = { navController.navigateUp() })
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


    }
}

