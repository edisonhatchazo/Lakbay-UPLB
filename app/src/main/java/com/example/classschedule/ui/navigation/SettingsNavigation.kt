package com.example.classschedule.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.classschedule.ui.settings.SettingsDestination
import com.example.classschedule.ui.settings.SettingsScreen
import com.example.classschedule.ui.settings.colors.ColorHomeDestination
import com.example.classschedule.ui.settings.colors.ColorSchemeDetails
import com.example.classschedule.ui.settings.colors.ColorSchemeDetailsDestination
import com.example.classschedule.ui.settings.colors.ColorSchemeEdit
import com.example.classschedule.ui.settings.colors.ColorSchemeEditDestination
import com.example.classschedule.ui.settings.colors.ColorSchemeEntry
import com.example.classschedule.ui.settings.colors.ColorSchemeEntryDestination
import com.example.classschedule.ui.settings.colors.ColorSchemeHome
import com.example.classschedule.ui.settings.global.CollegeDirectory
import com.example.classschedule.ui.settings.global.ColorDirectory
import com.example.classschedule.ui.settings.global.DirectoryColorsDestination
import com.example.classschedule.ui.settings.global.DirectoryHomeDestination
import com.example.classschedule.ui.settings.global.RoutingDestination
import com.example.classschedule.ui.settings.global.RoutingSettings
import com.example.classschedule.ui.theme.ThemeMode

@Composable
fun SettingsNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    openDrawer: () -> Unit,
    onThemeChange: (ThemeMode) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = SettingsDestination.route,
        modifier = modifier
    ) {
        composable(route = SettingsDestination.route){
            SettingsScreen(
                navigateToColorEntry = {navController.navigate(ColorHomeDestination.route)},
                openDrawer = openDrawer,
                navigateToCollegeColors = {navController.navigate(DirectoryHomeDestination.route)},
                navigateToRoutingSettings = {navController.navigate(RoutingDestination.route)},
                navigateToRoutesColors = {},
                onThemeChange = onThemeChange
            )
        }
        composable(route = ColorHomeDestination.route){
            ColorSchemeHome(
                onNavigateUp = { navController.navigateUp() },
                navigateToColorEntry = {navController.navigate(ColorSchemeEntryDestination.route)},
                navigateToColorDetails = {navController.navigate("${ColorSchemeDetailsDestination.route}/${it}") },
            )
        }
        composable(route = ColorSchemeEntryDestination.route){
            ColorSchemeEntry(
                onNavigateUp = { navController.navigateUp() },
                navigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = ColorSchemeDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(ColorSchemeDetailsDestination.COLORIDARG){
                type = NavType.IntType
            })
        ){
            ColorSchemeDetails(
                navigateBack = { navController.navigateUp()},
                navigateToEditColorScheme = {navController.navigate("${ColorSchemeEditDestination.route}/${it}")}
            )
        }
        composable(
            route = ColorSchemeEditDestination.routeWithArgs,
            arguments = listOf(navArgument(ColorSchemeEditDestination.COLORIDARG){
                type = NavType.IntType
            })
        ){
            ColorSchemeEdit(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() })
        }

        composable(route = DirectoryHomeDestination.route){
            CollegeDirectory(
                onNavigateUp = { navController.navigateUp() },
                navigateToColorDetails = {collegeFullName, previousColorId ->
                    navController.navigate("${DirectoryColorsDestination.route}/$collegeFullName/$previousColorId")},
            )
        }

        composable(route = RoutingDestination.route){
            RoutingSettings(
                onNavigateUp = { navController.navigateUp() },
            )
        }
        composable(
            route = DirectoryColorsDestination.routeWithArgs,
            arguments = listOf(
                navArgument(DirectoryColorsDestination.COLLEGE_ARG) { type = NavType.StringType },
                navArgument(DirectoryColorsDestination.COLORID_ARG) { type = NavType.IntType }
            )
        ){
            ColorDirectory(
                onNavigateUp = { navController.navigateUp() }
            )
        }

    }
}
