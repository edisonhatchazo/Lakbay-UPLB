package com.edison.lakbayuplb.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.edison.lakbayuplb.ui.about.AboutGuideMapDestination
import com.edison.lakbayuplb.ui.about.GuideMapAbout
import com.edison.lakbayuplb.ui.about.MyOwnPins
import com.edison.lakbayuplb.ui.about.MyOwnPinsDestination
import com.edison.lakbayuplb.ui.buildingScreens.pins.PinsDetailsDestination
import com.edison.lakbayuplb.ui.buildingScreens.pins.PinsDetailsScreen
import com.edison.lakbayuplb.ui.buildingScreens.pins.PinsEditDestination
import com.edison.lakbayuplb.ui.buildingScreens.pins.PinsEditScreen
import com.edison.lakbayuplb.ui.buildingScreens.pins.PinsEntryDestination
import com.edison.lakbayuplb.ui.buildingScreens.pins.PinsEntryScreen
import com.edison.lakbayuplb.ui.buildingScreens.pins.PinsHomeDestination
import com.edison.lakbayuplb.ui.buildingScreens.pins.PinsScreen
import com.edison.lakbayuplb.ui.map.GuideMapDestination
import com.edison.lakbayuplb.ui.map.GuideMapScreen

@Composable
fun PinsNavHost(
    navController: NavHostController,
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
                navigateToAboutPins = {navController.navigate(MyOwnPinsDestination.route)},
                openDrawer = openDrawer
            )
        }

        composable(route = MyOwnPinsDestination.route){
            MyOwnPins(
                navigateBack = { navController.navigateUp() },
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
                navigateBack = { navController.navigateUp() },
                navigateToAboutMap = {navController.navigate(AboutGuideMapDestination.route)}
            )
        }
        composable(route = AboutGuideMapDestination.route){
            GuideMapAbout(
                navigateBack = { navController.navigateUp() },
            )
        }
    }
}