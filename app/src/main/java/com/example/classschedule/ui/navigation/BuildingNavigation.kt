package com.example.classschedule.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.classschedule.ui.buildingScreens.uplb.buildings.BuildingDetailsDestination
import com.example.classschedule.ui.buildingScreens.uplb.buildings.BuildingDetailsScreen
import com.example.classschedule.ui.buildingScreens.uplb.buildings.BuildingEditDestination
import com.example.classschedule.ui.buildingScreens.uplb.buildings.BuildingEditScreen
import com.example.classschedule.ui.buildingScreens.uplb.buildings.BuildingEntryDestination
import com.example.classschedule.ui.buildingScreens.uplb.buildings.BuildingEntryScreen
import com.example.classschedule.ui.buildingScreens.uplb.buildings.BuildingHomeDestination
import com.example.classschedule.ui.buildingScreens.uplb.buildings.BuildingHomeScreen
import com.example.classschedule.ui.buildingScreens.uplb.rooms.ClassroomEditDestination
import com.example.classschedule.ui.buildingScreens.uplb.rooms.ClassroomEntryDestination
import com.example.classschedule.ui.buildingScreens.uplb.rooms.RoomDetailsDestination
import com.example.classschedule.ui.buildingScreens.uplb.rooms.RoomDetailsScreen
import com.example.classschedule.ui.buildingScreens.uplb.rooms.RoomEditScreen
import com.example.classschedule.ui.buildingScreens.uplb.rooms.RoomEntryScreen
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
                openDrawer = openDrawer,
                navigateToBuildingEntry = { navController.navigate(BuildingEntryDestination.route)}
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
                navigateBack = { navController.navigateUp() },
                navigateToMap = {navController.navigate("${GuideMapDestination.route}/${it}")},
                navigateToBuildingEdit = {navController.navigate("${BuildingEditDestination.route}/${it}") },
                navigateToClassroomEntry = {navController.navigate("${ClassroomEntryDestination.route}/${it}") }
            )
        }

        composable(
            route = BuildingEditDestination.routeWithArgs,
            arguments = listOf(navArgument(BuildingEditDestination.BUILDINGIDARG){
                type = NavType.IntType
            })
        ){
            BuildingEditScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() })
        }

        composable(route = BuildingEntryDestination.route){
            BuildingEntryScreen(
                navigateBack = { navController.popBackStack()},
                onNavigateUp = { navController.navigateUp()}
            )
        }


        composable(
            route = RoomDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(RoomDetailsDestination.ROOMIDARG){
                type = NavType.IntType
            })
        ){
            RoomDetailsScreen(navigateBack = {navController.navigateUp()},
                navigateToMap = {navController.navigate("${GuideMapDestination.route}/${it}")},
                navigateToClassroomEdit = {navController.navigate("${ClassroomEditDestination.route}/${it}") },
            )

        }

        composable(
            route = ClassroomEntryDestination.routeWithArgs,
            arguments = listOf(navArgument(ClassroomEntryDestination.BUILDINGIDARG){
                type = NavType.IntType
            })
        ){
            RoomEntryScreen(
                navigateBack = { navController.popBackStack()},
                onNavigateUp = { navController.navigateUp()}
            )

        }

        composable(
            route = ClassroomEditDestination.routeWithArgs,
            arguments = listOf(navArgument(ClassroomEditDestination.CLASROOMIDARG){
                type = NavType.IntType
            })
        ){
            RoomEditScreen(
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

