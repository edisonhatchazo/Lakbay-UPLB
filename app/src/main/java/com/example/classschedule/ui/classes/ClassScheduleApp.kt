package com.example.classschedule.ui.classes

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.classschedule.ui.navigation.ClassScheduleNavHost

@Composable
fun ClassScheduleApp(navController: NavHostController = rememberNavController()) {
    ClassScheduleNavHost(navController = navController)
}
