package com.example.classschedule.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.classschedule.ui.buildingScreens.uplb.BuildingDetailsDestination
import com.example.classschedule.ui.buildingScreens.uplb.BuildingDetailsScreen
import com.example.classschedule.ui.buildingScreens.uplb.BuildingHomeDestination
import com.example.classschedule.ui.buildingScreens.uplb.BuildingHomeScreen
import com.example.classschedule.ui.buildingScreens.uplb.RoomDetailsDestination
import com.example.classschedule.ui.buildingScreens.uplb.RoomDetailsScreen
import com.example.classschedule.ui.map.GuideMapDestination
import com.example.classschedule.ui.map.GuideMapScreen

@Composable
fun BuildingNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    openDrawer: () -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = BuildingHomeDestination.route,
        modifier = modifier
    ) {
        composable(route = BuildingHomeDestination.route) {
            BuildingHomeScreen(
                navigateToBuildingDetails = { navController.navigate("${BuildingDetailsDestination.route}/${it}") },
                navigateToRoomDetails = {navController.navigate("${RoomDetailsDestination.route}/${it}") },
                openDrawer = openDrawer
            )
        }

        composable(
            route = RoomDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(RoomDetailsDestination.ROOMIDARG){
                type = NavType.IntType
            })
        ){
            RoomDetailsScreen(navigateBack = {navController.navigateUp()},
                navigateToMap = {navController.navigate("${GuideMapDestination.route}/${it}")})

        }

        composable(
            route = BuildingDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(BuildingDetailsDestination.BUILDINGIDARG){
                type = NavType.IntType
            })
        ){
            BuildingDetailsScreen(
                navigateToRoomDetails = {navController.navigate("${RoomDetailsDestination.route}/${it}") },
                navigateBack = { navController.navigateUp() },
                navigateToMap = {navController.navigate("${GuideMapDestination.route}/${it}")},
            )
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

