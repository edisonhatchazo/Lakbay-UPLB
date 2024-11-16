package com.edison.lakbayuplb.ui.navigation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.edison.lakbayuplb.ui.classes.ClassHomeDestination
import com.edison.lakbayuplb.ui.classes.ClassHomeScheduleDestination
import com.edison.lakbayuplb.ui.classes.ClassHomeScreen
import com.edison.lakbayuplb.ui.classes.ClassScheduleDetailsDestination
import com.edison.lakbayuplb.ui.classes.ClassScheduleDetailsScreen
import com.edison.lakbayuplb.ui.classes.ClassScheduleEditDestination
import com.edison.lakbayuplb.ui.classes.ClassScheduleEditScreen
import com.edison.lakbayuplb.ui.classes.ClassScheduleEntryDestination
import com.edison.lakbayuplb.ui.classes.ClassScheduleEntryScreen
import com.edison.lakbayuplb.ui.classes.ClassScheduleHomeScreen
import com.edison.lakbayuplb.ui.exam.ExamDetailsDestination
import com.edison.lakbayuplb.ui.exam.ExamDetailsScreen
import com.edison.lakbayuplb.ui.exam.ExamEditDestination
import com.edison.lakbayuplb.ui.map.GuideMapDestination
import com.edison.lakbayuplb.ui.map.GuideMapScreen

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
                navigateToClassScheduleEntry = { navController.navigate(
                    ClassScheduleEntryDestination.route)},
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
    val classId = (LocalContext.current as? Activity)?.intent?.getIntExtra("classId", -1) ?: -1
    val examId = (LocalContext.current as? Activity)?.intent?.getIntExtra("examId", -1) ?: -1
    LaunchedEffect(classId) {
        if (classId != -1) {
            navController.navigate(ClassHomeScheduleDestination.route) {
                popUpTo(ClassHomeScheduleDestination.route) { inclusive = false }
            }
            navController.navigate("${ClassScheduleDetailsDestination.route}/$classId")
        }
    }
    LaunchedEffect(examId) {
        if (examId != -1) {
            navController.navigate(ClassHomeScheduleDestination.route) {
                popUpTo(ClassHomeScheduleDestination.route) { inclusive = false }
            }
            navController.navigate("${ExamDetailsDestination.route}/$examId")
        }
    }

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
        //Composable for Notifications
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

    }
}
