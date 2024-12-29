package com.edison.lakbayuplb.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.edison.lakbayuplb.ui.about.AboutGuideMapDestination
import com.edison.lakbayuplb.ui.about.BuildingsAndRooms
import com.edison.lakbayuplb.ui.about.BuildingsAndRoomsDestination
import com.edison.lakbayuplb.ui.about.GuideMapAbout
import com.edison.lakbayuplb.ui.buildingScreens.uplb.buildings.BuildingDetailsDestination
import com.edison.lakbayuplb.ui.buildingScreens.uplb.buildings.BuildingDetailsScreen
import com.edison.lakbayuplb.ui.buildingScreens.uplb.buildings.BuildingEditDestination
import com.edison.lakbayuplb.ui.buildingScreens.uplb.buildings.BuildingEditScreen
import com.edison.lakbayuplb.ui.buildingScreens.uplb.buildings.BuildingEntryDestination
import com.edison.lakbayuplb.ui.buildingScreens.uplb.buildings.BuildingEntryScreen
import com.edison.lakbayuplb.ui.buildingScreens.uplb.buildings.BuildingHomeDestination
import com.edison.lakbayuplb.ui.buildingScreens.uplb.buildings.BuildingHomeScreen
import com.edison.lakbayuplb.ui.buildingScreens.uplb.rooms.ClassroomEditDestination
import com.edison.lakbayuplb.ui.buildingScreens.uplb.rooms.ClassroomEntryDestination
import com.edison.lakbayuplb.ui.buildingScreens.uplb.rooms.RoomDetailsDestination
import com.edison.lakbayuplb.ui.buildingScreens.uplb.rooms.RoomDetailsScreen
import com.edison.lakbayuplb.ui.buildingScreens.uplb.rooms.RoomEditScreen
import com.edison.lakbayuplb.ui.buildingScreens.uplb.rooms.RoomEntryScreen
import com.edison.lakbayuplb.ui.map.GuideMapDestination
import com.edison.lakbayuplb.ui.map.GuideMapScreen

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
                navigateToBuildingEntry = { navController.navigate(BuildingEntryDestination.route)},
                navigateToAboutPage = {navController.navigate(BuildingsAndRoomsDestination.route)}
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
                navigateToClassroomEntry = {navController.navigate("${ClassroomEntryDestination.route}/${it}") },
                navigateToAboutPage = {navController.navigate(BuildingsAndRoomsDestination.route)}
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
                onNavigateUp = { navController.navigateUp() },
                navigateToAboutPage = {navController.navigate(BuildingsAndRoomsDestination.route)}
            )
        }

        composable(route = BuildingEntryDestination.route){
            BuildingEntryScreen(
                navigateBack = { navController.popBackStack()},
                onNavigateUp = { navController.navigateUp()},
                navigateToAboutPage = {navController.navigate(BuildingsAndRoomsDestination.route)}
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
                navigateToAboutPage = {navController.navigate(BuildingsAndRoomsDestination.route)}
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
                onNavigateUp = { navController.navigateUp()},
                navigateToAboutPage = {navController.navigate(BuildingsAndRoomsDestination.route)}
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
                onNavigateUp = { navController.navigateUp() },
                navigateToAboutPage = {navController.navigate(BuildingsAndRoomsDestination.route)}
            )
        }

        composable(
            route = GuideMapDestination.routeWithArgs,
            arguments = listOf(navArgument(GuideMapDestination.MAPDATAIDARG){
                type = NavType.IntType
            })
        ) {
            GuideMapScreen(
                navigateBack = { navController.navigateUp() },
                navigateToAboutMap = {navController.navigate(AboutGuideMapDestination.route)}
            )
        }


        composable(route = BuildingsAndRoomsDestination.route){
            BuildingsAndRooms(
                navigateBack = { navController.navigateUp() },
            )
        }

        composable(route = AboutGuideMapDestination.route){
            GuideMapAbout(
                navigateBack = { navController.navigateUp() },
            )
        }
    }
}

