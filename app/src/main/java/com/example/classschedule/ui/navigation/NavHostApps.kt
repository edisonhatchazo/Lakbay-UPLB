package com.example.classschedule.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun ScheduleApp( openDrawer: () -> Unit, navController: NavHostController = rememberNavController()) {
    ScheduleNavHost(navController = navController,openDrawer = openDrawer)
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
fun PinsApp(mainNavController: NavHostController, openDrawer: () -> Unit, navController: NavHostController = rememberNavController()) {
    PinsNavHost(navController = navController, mainNavController = mainNavController, openDrawer = openDrawer)
}

@Composable
fun SettingsApp(openDrawer: () -> Unit, navController: NavHostController = rememberNavController()) {
    SettingsNavHost(navController = navController, openDrawer = openDrawer)
}