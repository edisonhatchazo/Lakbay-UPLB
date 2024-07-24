package com.example.classschedule.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.classschedule.ui.classes.ClassHomeDestination
import com.example.classschedule.ui.classes.ClassHomeScheduleDestination
import com.example.classschedule.ui.classes.ClassHomeScreen
import com.example.classschedule.ui.classes.ClassScheduleDetailsDestination
import com.example.classschedule.ui.classes.ClassScheduleDetailsScreen
import com.example.classschedule.ui.classes.ClassScheduleEditDestination
import com.example.classschedule.ui.classes.ClassScheduleEditScreen
import com.example.classschedule.ui.classes.ClassScheduleEntryDestination
import com.example.classschedule.ui.classes.ClassScheduleEntryScreen
import com.example.classschedule.ui.classes.ClassScheduleHomeScreen
import com.example.classschedule.ui.map.GuideMapDestination
import com.example.classschedule.ui.map.GuideMapScreen

@Composable
fun ClassScheduleNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    openDrawer: () -> Unit,
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
                },
                openDrawer = openDrawer
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
                navigateBack = {navController.navigateUp()},
                navigateToMap = {navController.navigate("${GuideMapDestination.route}/${it}")},
            )
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


@Composable
fun ScheduleNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    openDrawer: () -> Unit,
){
    NavHost(
        navController = navController,
        startDestination = ClassHomeScheduleDestination.route,
        modifier = modifier
    ){
        composable(route = ClassHomeScheduleDestination.route) {
            ClassScheduleHomeScreen(
                navigateToScheduleEntry = { navController.navigate(ClassScheduleEntryDestination.route) },
                navigateToScheduleUpdate = {
                    navController.navigate("${ClassScheduleDetailsDestination.route}/$it")
                },
                openDrawer = openDrawer
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
                navigateBack = {navController.navigateUp()},
                navigateToMap = {navController.navigate("${GuideMapDestination.route}/${it}")},
            )
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
