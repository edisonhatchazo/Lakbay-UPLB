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
import com.example.classschedule.ui.buildingScreens.uplb.BuildingDetailsDestination
import com.example.classschedule.ui.buildingScreens.uplb.BuildingDetailsScreen
import com.example.classschedule.ui.buildingScreens.uplb.BuildingHomeDestination
import com.example.classschedule.ui.buildingScreens.uplb.BuildingHomeScreen
import com.example.classschedule.ui.buildingScreens.uplb.RoomDetailsDestination
import com.example.classschedule.ui.buildingScreens.uplb.RoomDetailsScreen

@Composable
fun BuildingNavHost(
    navController: NavHostController,
    mainNavController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = BuildingHomeDestination.route,
        modifier = modifier
    ) {
        composable(route = BuildingHomeDestination.route) {
            BuildingHomeScreen(
                navigateToBuildingDetails = { navController.navigate("${BuildingDetailsDestination.route}/${it}") },
                navigateToPinsHomeDestination = {navController.navigate(PinsHomeDestination.route) },
                navigateToRoomDetails = {navController.navigate("${RoomDetailsDestination.route}/${it}") },
            )
        }

        composable(
            route = BuildingDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(BuildingDetailsDestination.BUILDINGIDARG){
                type = NavType.IntType
            })
        ){
            BuildingDetailsScreen(
                navigateToRoomDetails = {navController.navigate("${RoomDetailsDestination.route}/${it}") },
                navigateBack = { navController.navigateUp() })
        }

        composable(
            route = RoomDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(RoomDetailsDestination.ROOMIDARG){
                type = NavType.IntType
            })
        ){
            RoomDetailsScreen(navigateBack = {navController.navigateUp()},mainNavController = mainNavController)

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

