package com.edison.lakbayuplb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import com.edison.lakbayuplb.ui.theme.LakbayUPLBTheme
import com.edison.lakbayuplb.ui.theme.ThemeMode
import com.edison.lakbayuplb.ui.theme.ThemePreferences


class MainActivity : ComponentActivity() {
    private lateinit var themePreferences: ThemePreferences
    private var themeMode by mutableStateOf(ThemeMode.SYSTEM)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        themePreferences = ThemePreferences(this)
        themeMode = themePreferences.getThemeMode()
        setContent {
            LakbayUPLBTheme(themeMode = themeMode) {
                LakbayApp(onThemeChange = { newTheme ->
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