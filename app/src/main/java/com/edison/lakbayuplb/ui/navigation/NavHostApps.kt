package com.edison.lakbayuplb.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.edison.lakbayuplb.ui.about.AboutNavHost
import com.edison.lakbayuplb.ui.theme.ThemeMode

@Composable
fun ScheduleApp( openDrawer: () -> Unit, navController: NavHostController = rememberNavController()) {
    ScheduleNavHost(navController = navController,openDrawer = openDrawer)
}

@Composable
fun AboutApp(openDrawer: () -> Unit,navController: NavHostController = rememberNavController()){
    AboutNavHost(navController = navController, openDrawer = openDrawer)
}

@Composable
fun ClassScheduleApp(openDrawer: () -> Unit, navController: NavHostController = rememberNavController()) {
    ClassScheduleNavHost(navController = navController, openDrawer = openDrawer)
}

@Composable
fun BuildingApp(openDrawer: () -> Unit, navController: NavHostController = rememberNavController()) {
    BuildingNavHost(navController = navController, openDrawer = openDrawer)
}

@Composable
fun ExamHomeApp(openDrawer: () -> Unit, navController: NavHostController = rememberNavController()) {
    ExamHomeNavHost(navController = navController, openDrawer = openDrawer)
}

@Composable
fun ExamScheduleApp(openDrawer: () -> Unit, navController: NavHostController = rememberNavController()) {
    ExamScheduleNavHost(navController = navController, openDrawer = openDrawer)
}

@Composable
fun PinsApp(openDrawer: () -> Unit, navController: NavHostController = rememberNavController()) {
    PinsNavHost(navController = navController, openDrawer = openDrawer)
}

@Composable
fun SettingsApp(openDrawer: () -> Unit, navController: NavHostController = rememberNavController(), onThemeChange: (ThemeMode) -> Unit) {
    SettingsNavHost(navController = navController, openDrawer = openDrawer, onThemeChange = onThemeChange)
}