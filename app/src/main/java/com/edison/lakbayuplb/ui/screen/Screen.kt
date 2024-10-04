package com.edison.lakbayuplb.ui.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Schedules : Screen("schedule", "Schedule", Icons.Default.Home) // Updated route
    data object Classes : Screen("classes", "Classes", Icons.Default.List)
    data object ExamSchedule : Screen("exam_schedule", "Exam Schedule", Icons.Default.List)
    data object Exams : Screen("exams", "Exams", Icons.Default.List)
    data object Buildings : Screen("buildings", "Buildings", Icons.Default.Build)
    data object Pins : Screen("pins", "Pins", Icons.Default.LocationOn)
    data object Map : Screen("map", "Map", Icons.Default.Place)
    data object Settings: Screen("settings","Settings",Icons.Default.Settings)
}