package com.example.classschedule.ui.screen

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.classschedule.ui.classes.ClassScheduleApp

@Composable
fun ClassesScreen(mainNavController: NavHostController) {
    ClassScheduleApp(mainNavController)
}