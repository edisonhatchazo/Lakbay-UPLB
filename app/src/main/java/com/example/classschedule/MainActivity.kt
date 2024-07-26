package com.example.classschedule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import com.example.classschedule.ui.navigation.MyApp
import com.example.classschedule.ui.theme.ClassScheduleTheme
import com.example.classschedule.ui.theme.ThemeMode
import com.example.classschedule.ui.theme.ThemePreferences


class MainActivity : ComponentActivity() {
    private lateinit var themePreferences: ThemePreferences
    private var themeMode by mutableStateOf(ThemeMode.SYSTEM)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        themePreferences = ThemePreferences(this)
        themeMode = themePreferences.getThemeMode()
        setContent {
            ClassScheduleTheme(themeMode = themeMode) {
                MyApp(onThemeChange = { newTheme ->
                    themeMode = newTheme
                    themePreferences.setThemeMode(newTheme)
                })
            }
        }
    }
    private fun enableEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }
}

