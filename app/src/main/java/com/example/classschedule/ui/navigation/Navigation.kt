package com.example.classschedule.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.classschedule.ui.map.MainMapScreen
import com.example.classschedule.ui.screen.Screen
import com.example.classschedule.ui.theme.ThemeMode

@Composable
fun Navigation(navController: NavHostController, openDrawer: () -> Unit, modifier: Modifier = Modifier, onThemeChange: (ThemeMode) -> Unit) {
    NavHost(navController, startDestination = Screen.Schedules.route, modifier = modifier) {
        composable(Screen.Schedules.route) { ScheduleApp(openDrawer = openDrawer) }
        composable(Screen.Classes.route) { ClassScheduleApp(openDrawer = openDrawer) }
        composable(Screen.Buildings.route) { BuildingApp(openDrawer = openDrawer) }
        composable(Screen.ExamSchedule.route) { ExamScheduleApp(openDrawer = openDrawer)}
        composable(Screen.Exams.route){ ExamHomeApp(openDrawer = openDrawer)}
        composable(Screen.Pins.route){ PinsApp(mainNavController = navController, openDrawer = openDrawer)}
        composable(Screen.Map.route) { MainMapScreen(openDrawer = openDrawer) }
        composable(Screen.Settings.route){SettingsApp(openDrawer = openDrawer, onThemeChange = onThemeChange)}
    }
}