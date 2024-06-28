package com.example.classschedule.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.classschedule.ui.exam.ExamDetailsDestination
import com.example.classschedule.ui.exam.ExamDetailsScreen
import com.example.classschedule.ui.exam.ExamEditDestination
import com.example.classschedule.ui.exam.ExamEditScreen
import com.example.classschedule.ui.exam.ExamEntryDestination
import com.example.classschedule.ui.exam.ExamEntryScreen
import com.example.classschedule.ui.exam.ExamScheduleDestination
import com.example.classschedule.ui.exam.ExamScheduleScreen
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
    mainNavController: NavHostController,
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
                },
                navigateToExamHomeDestination = { navController.navigate(ExamScheduleDestination.route)}
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
                navigateBack = {navController.navigateUp()},
                mainNavController = mainNavController

            )
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

        composable(route = ExamScheduleDestination.route) {
            ExamScheduleScreen(
                navigateToScheduleEntry = { navController.navigate(ExamEntryDestination.route) },
                navigateToScheduleUpdate = {
                    navController.navigate("${ExamDetailsDestination.route}/$it")
                },
                navigateToScheduleHomeDestination = { navController.navigate(ScheduleHomeDestination.route)}
            )
        }

        composable(route = ExamEntryDestination.route){
            ExamEntryScreen(
                navigateBack = { navController.popBackStack()},
                onNavigateUp = { navController.navigateUp()})
        }

        composable(
            route = ExamDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(ExamDetailsDestination.SCHEDULEIDARG){
                type = NavType.IntType
            })
        ){
            ExamDetailsScreen(
                navigateToEditExam = {navController.navigate("${ExamEditDestination.route}/$it")},
                navigateBack = {navController.navigateUp()},
                mainNavController = mainNavController)
        }
        composable(
            route = ExamEditDestination.routeWithArgs,
            arguments = listOf(navArgument(ExamEditDestination.SCHEDULEIDARG){
                type = NavType.IntType
            })
        ){
            ExamEditScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() })
        }
    }
}