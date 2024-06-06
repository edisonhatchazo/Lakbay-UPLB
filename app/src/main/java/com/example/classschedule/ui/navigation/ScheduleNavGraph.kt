package com.example.classschedule.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.classschedule.ui.home.ScheduleDetailsDestination
import com.example.classschedule.ui.home.ScheduleDetailsScreen
import com.example.classschedule.ui.home.ScheduleEditDestination
import com.example.classschedule.ui.home.ScheduleEditScreen
import com.example.classschedule.ui.home.ScheduleEntryDestination
import com.example.classschedule.ui.home.ScheduleEntryScreen
import com.example.classschedule.ui.home.ScheduleHomeDestination
import com.example.classschedule.ui.home.ScheduleScreen

@Composable
fun ScheduleNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = ScheduleHomeDestination.route,
        modifier = modifier
    ) {
        composable(route = ScheduleHomeDestination.route) {
            ScheduleScreen(
                navigateToScheduleEntry = { navController.navigate(ScheduleEntryDestination.route) },
                navigateToScheduleUpdate = {
                    navController.navigate("${ScheduleDetailsDestination.route}/$it")
                }
            )
        }
        composable(route = ScheduleEntryDestination.route){
            ScheduleEntryScreen(
                navigateBack = { navController.popBackStack()},
                onNavigateUp = { navController.navigateUp()})
        }
        composable(
            route = ScheduleDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(ScheduleDetailsDestination.SCHEDULEIDARG){
                type = NavType.IntType
            })
        ){
            ScheduleDetailsScreen(
                navigateToEditSchedule = {navController.navigate("${ScheduleEditDestination.route}/$it")},
                navigateBack = {navController.navigateUp()})
        }
        composable(
            route = ScheduleEditDestination.routeWithArgs,
            arguments = listOf(navArgument(ScheduleEditDestination.SCHEDULEIDARG){
                type = NavType.IntType
            })
        ){
            ScheduleEditScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() })
        }
    }
}