package com.edison.lakbayuplb.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.edison.lakbayuplb.ui.about.AboutHome
import com.edison.lakbayuplb.ui.about.AboutHomeDestination

@Composable
fun AboutNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    openDrawer: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = AboutHomeDestination.route,
        modifier = modifier
    ) {
        composable(route = AboutHomeDestination.route){
            AboutHome(
                openDrawer = openDrawer,
            )
        }
    }
}