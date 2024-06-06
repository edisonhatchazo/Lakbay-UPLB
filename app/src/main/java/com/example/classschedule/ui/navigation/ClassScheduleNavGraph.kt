package com.example.classschedule.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.classschedule.ui.classes.ClassScheduleDetailsDestination
import com.example.classschedule.ui.classes.ClassScheduleDetailsScreen
import com.example.classschedule.ui.classes.ClassScheduleEditDestination
import com.example.classschedule.ui.classes.ClassScheduleEditScreen
import com.example.classschedule.ui.classes.ClassScheduleEntryDestination
import com.example.classschedule.ui.classes.ClassScheduleEntryScreen
import com.example.classschedule.ui.classes.ClassHomeDestination
import com.example.classschedule.ui.classes.ClassHomeScreen
import com.example.classschedule.ui.home.ScheduleHomeDestination

@Composable
fun ClassScheduleNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
){
    NavHost(
        navController = navController,
        startDestination = ClassHomeDestination.route,
        modifier = modifier
    ){
        composable(route = ClassHomeDestination.route){
            ClassHomeScreen(
                navigateToClassScheduleEntry = { navController.navigate(ClassScheduleEntryDestination.route)},
                navigateToClassScheduleUpdate = {
                    navController.navigate("${ClassScheduleDetailsDestination.route}/${it}")
                }
            )
        }
        composable(route = ClassScheduleEntryDestination.route){
            ClassScheduleEntryScreen(
                navigateBack = { navController.popBackStack()},
                onNavigateUp = { navController.navigateUp()})
        }
        composable(
            route = ClassScheduleDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(ClassScheduleDetailsDestination.CLASSSCHEDULEIDARG){
                type = NavType.IntType
            })


        ){
            ClassScheduleDetailsScreen(
                navigateToEditClassSchedule = {navController.navigate("${ClassScheduleEditDestination.route}/$it")},
                navigateBack = {navController.navigateUp()})
        }
        composable(
            route = ClassScheduleEditDestination.routeWithArgs,
            arguments = listOf(navArgument(ClassScheduleEditDestination.CLASSSCHEDULEIDARG){
                type = NavType.IntType
            })
        ){
            ClassScheduleEditScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() })
        }
    }
}
