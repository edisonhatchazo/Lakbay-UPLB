package com.example.classschedule.ui.home

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.classschedule.ui.navigation.ScheduleNavHost

@Composable
fun ScheduleApp(mainNavController: NavHostController, navController: NavHostController = rememberNavController()) {
    ScheduleNavHost(navController = navController, mainNavController = mainNavController)
}

