package com.edison.lakbayuplb.ui.about

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun AboutNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    openDrawer: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = AboutHomeDestination.route,
        modifier = modifier
    ) {
        composable(route = AboutHomeDestination.route){
            AboutHome(
                openDrawer = openDrawer,
                navigateToLakbay = {navController.navigate(LakbayHomeScreenDestination.route)},
                navigateToBuildingsAndRooms = {navController.navigate(BuildingsAndRoomsDestination.route)},
                navigateToClasses = {navController.navigate(AboutClassesDestination.route)},
                navigateToExams = {navController.navigate(AboutExamsDestination.route)},
                navigateToAboutGuideMap = {navController.navigate(AboutGuideMapDestination.route)},
                navigateToMyOwnPins = {navController.navigate(MyOwnPinsDestination.route)},
                navigateToSettingsAndCustomization = {navController.navigate(SettingsAndCustomizationDestination.route)},
            )
        }

        composable(route = LakbayHomeScreenDestination.route){
            LakbayScreenAbout(
                navigateBack = { navController.navigateUp() },
            )
        }

        composable(route = BuildingsAndRoomsDestination.route){
            BuildingsAndRooms(
                navigateBack = { navController.navigateUp() },
            )
        }

        composable(route = AboutClassesDestination.route){
            AboutClasses(
                navigateBack = { navController.navigateUp() },
            )
        }

        composable(route = AboutExamsDestination.route){
            AboutExams(navigateBack = {navController.navigateUp()})
        }

        composable(route = AboutGuideMapDestination.route){
            GuideMapAbout(
                navigateBack = { navController.navigateUp() },
            )
        }

        composable(route = MyOwnPinsDestination.route){
            MyOwnPins(
                navigateBack = { navController.navigateUp() },
            )
        }

        composable(route = SettingsAndCustomizationDestination.route){
            SettingsAndCustomization(
                navigateBack = { navController.navigateUp() },
            )
        }
    }
}