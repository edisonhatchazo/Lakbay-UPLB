package com.edison.lakbayuplb.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.edison.lakbayuplb.ui.about.AboutExams
import com.edison.lakbayuplb.ui.about.AboutExamsDestination
import com.edison.lakbayuplb.ui.about.AboutGuideMapDestination
import com.edison.lakbayuplb.ui.about.GuideMapAbout
import com.edison.lakbayuplb.ui.exam.ExamDetailsDestination
import com.edison.lakbayuplb.ui.exam.ExamDetailsScreen
import com.edison.lakbayuplb.ui.exam.ExamEditDestination
import com.edison.lakbayuplb.ui.exam.ExamEditScreen
import com.edison.lakbayuplb.ui.exam.ExamEntryDestination
import com.edison.lakbayuplb.ui.exam.ExamEntryScreen
import com.edison.lakbayuplb.ui.exam.ExamHomeDestination
import com.edison.lakbayuplb.ui.exam.ExamHomeScreen
import com.edison.lakbayuplb.ui.exam.ExamScheduleDestination
import com.edison.lakbayuplb.ui.exam.ExamScheduleScreen
import com.edison.lakbayuplb.ui.map.GuideMapDestination
import com.edison.lakbayuplb.ui.map.GuideMapScreen

@Composable
fun ExamScheduleNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    openDrawer: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = ExamScheduleDestination.route,
        modifier = modifier
    ) {
        composable(route = ExamScheduleDestination.route) {
            ExamScheduleScreen(
                navigateToScheduleEntry = { navController.navigate(ExamEntryDestination.route) },
                navigateToScheduleUpdate = {
                    navController.navigate("${ExamDetailsDestination.route}/$it")
                },
                openDrawer = openDrawer
            )
        }

        composable(route = AboutExamsDestination.route){
            AboutExams(
                navigateBack = { navController.navigateUp() },
            )
        }

        composable(route = ExamEntryDestination.route){
            ExamEntryScreen(
                navigateBack = { navController.popBackStack()},
                onNavigateUp = { navController.navigateUp()},
                navigateToAboutPage = {navController.navigate(AboutExamsDestination.route)}
                )
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
                navigateToAboutPage = {navController.navigate(AboutExamsDestination.route)}
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
                onNavigateUp = { navController.navigateUp() },
                navigateToAboutPage = {navController.navigate(AboutExamsDestination.route)}
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
        composable(route = AboutGuideMapDestination.route){
            GuideMapAbout(
                navigateBack = { navController.navigateUp() },
            )
        }
    }
}

@Composable
fun ExamHomeNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    openDrawer: () -> Unit,
    ) {
    NavHost(
        navController = navController,
        startDestination = ExamHomeDestination.route,
        modifier = modifier
    ) {
        composable(route = ExamHomeDestination.route){
            ExamHomeScreen(
                navigateToExamScheduleEntry = { navController.navigate(ExamEntryDestination.route)},
                navigateToExamScheduleUpdate = {
                    navController.navigate("${ExamDetailsDestination.route}/${it}")
                },
                openDrawer = openDrawer,
                navigateToAboutPage = {navController.navigate(AboutExamsDestination.route)},
            )
        }

        composable(route = AboutExamsDestination.route){
            AboutExams(
                navigateBack = { navController.navigateUp() },
            )
        }

        composable(route = ExamEntryDestination.route){
            ExamEntryScreen(
                navigateBack = { navController.popBackStack()},
                onNavigateUp = { navController.navigateUp()},
                navigateToAboutPage = {navController.navigate(AboutExamsDestination.route)}
            )
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
                navigateToAboutPage = {navController.navigate(AboutExamsDestination.route)}
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
                onNavigateUp = { navController.navigateUp() },
                navigateToAboutPage = {navController.navigate(AboutExamsDestination.route)}
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
        composable(route = AboutGuideMapDestination.route){
            GuideMapAbout(
                navigateBack = { navController.navigateUp() },
            )
        }
    }
}