package com.example.classschedule.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.classschedule.ui.classes.ClassHomeDestination
import com.example.classschedule.ui.classes.ClassHomeScreen
import com.example.classschedule.ui.classes.ClassScheduleDetailsDestination
import com.example.classschedule.ui.classes.ClassScheduleDetailsScreen
import com.example.classschedule.ui.classes.ClassScheduleEditDestination
import com.example.classschedule.ui.classes.ClassScheduleEditScreen
import com.example.classschedule.ui.classes.ClassScheduleEntryDestination
import com.example.classschedule.ui.classes.ClassScheduleEntryScreen
import com.example.classschedule.ui.exam.ExamDetailsDestination
import com.example.classschedule.ui.exam.ExamDetailsScreen
import com.example.classschedule.ui.exam.ExamEditDestination
import com.example.classschedule.ui.exam.ExamEditScreen
import com.example.classschedule.ui.exam.ExamEntryDestination
import com.example.classschedule.ui.exam.ExamEntryScreen
import com.example.classschedule.ui.exam.ExamHomeDestination
import com.example.classschedule.ui.exam.ExamHomeScreen
import com.example.classschedule.ui.map.GuideMapDestination
import com.example.classschedule.ui.map.GuideMapScreen

@Composable
fun ClassScheduleNavHost(
    navController: NavHostController,
    mainNavController: NavHostController,
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
                },
                navigateToExamHomeDestination = { navController.navigate(ExamHomeDestination.route)}
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

        composable(route = ExamHomeDestination.route){
            ExamHomeScreen(
                navigateToExamScheduleEntry = { navController.navigate(ExamEntryDestination.route)},
                navigateToExamScheduleUpdate = {
                    navController.navigate("${ExamDetailsDestination.route}/${it}")
                },
                navigateToClassHomeDestination = { navController.navigate(ClassHomeDestination.route)}
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
                navigateToMap = {navController.navigate("${GuideMapDestination.route}/${it}")},
            )

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
