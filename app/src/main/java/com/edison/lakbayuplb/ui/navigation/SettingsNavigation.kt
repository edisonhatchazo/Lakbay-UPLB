package com.edison.lakbayuplb.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.edison.lakbayuplb.ui.settings.SettingsDestination
import com.edison.lakbayuplb.ui.settings.SettingsScreen
import com.edison.lakbayuplb.ui.settings.UIScreen
import com.edison.lakbayuplb.ui.settings.UserInterfaceDestination
import com.edison.lakbayuplb.ui.settings.colors.ColorHomeDestination
import com.edison.lakbayuplb.ui.settings.colors.ColorSchemeDetails
import com.edison.lakbayuplb.ui.settings.colors.ColorSchemeDetailsDestination
import com.edison.lakbayuplb.ui.settings.colors.ColorSchemeEdit
import com.edison.lakbayuplb.ui.settings.colors.ColorSchemeEditDestination
import com.edison.lakbayuplb.ui.settings.colors.ColorSchemeEntry
import com.edison.lakbayuplb.ui.settings.colors.ColorSchemeEntryDestination
import com.edison.lakbayuplb.ui.settings.colors.ColorSchemeHome
import com.edison.lakbayuplb.ui.settings.global.CollegeDirectory
import com.edison.lakbayuplb.ui.settings.global.ColorDirectory
import com.edison.lakbayuplb.ui.settings.global.DirectoryColorsDestination
import com.edison.lakbayuplb.ui.settings.global.DirectoryHomeDestination
import com.edison.lakbayuplb.ui.settings.global.RoutingDestination
import com.edison.lakbayuplb.ui.settings.global.RoutingSettings
import com.edison.lakbayuplb.ui.settings.global.TopAppBarColorSchemes
import com.edison.lakbayuplb.ui.settings.global.TopAppBarColorsDestination
import com.edison.lakbayuplb.ui.theme.ThemeMode

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
                openDrawer = openDrawer,
                navigateToUserInterface = {navController.navigate(UserInterfaceDestination.route)},
                navigateToRoutingSettings = {navController.navigate(RoutingDestination.route)}
            )
        }


        composable(route = UserInterfaceDestination.route){
            UIScreen(
                onNavigateUp = {navController.navigateUp()},
                navigateToColorEntry = {navController.navigate(ColorHomeDestination.route)},
                onThemeChange = onThemeChange,
                navigateToCollegeColors = {navController.navigate(DirectoryHomeDestination.route)},
                navigateToTopAppBarColors = {navController.navigate(TopAppBarColorsDestination.route)})
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

        composable(route = TopAppBarColorsDestination.route){
            TopAppBarColorSchemes(onNavigateUp = {navController.navigate(SettingsDestination.route) })
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
