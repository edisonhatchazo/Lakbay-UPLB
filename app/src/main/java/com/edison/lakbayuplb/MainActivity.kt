package com.edison.lakbayuplb

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.edison.lakbayuplb.ui.theme.LakbayUPLBTheme
import com.edison.lakbayuplb.ui.theme.ThemeMode
import com.edison.lakbayuplb.ui.theme.ThemePreferences


class MainActivity : ComponentActivity() {
    private lateinit var themePreferences: ThemePreferences
    private var themeMode by mutableStateOf(ThemeMode.SYSTEM)
    private val locationCode = 101
    private val requestCode = 100
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        themePreferences = ThemePreferences(this)
        themeMode = themePreferences.getThemeMode()
        checkAndRequestPermissions()
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

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun checkAndRequestPermissions() {
        val requiredPermissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        // Add FOREGROUND_SERVICE_LOCATION only for devices on API 34 (Android 14) or later
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            requiredPermissions.add(Manifest.permission.FOREGROUND_SERVICE_LOCATION)
        }

        // Request any permissions that haven't been granted yet
        val permissionsToRequest = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), requestCode)
        } else {
            // All foreground permissions granted, now check for background location
            checkBackgroundLocationPermission()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkBackgroundLocationPermission() {
        // Check if background location permission is already granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Show an explanation dialog
            showBackgroundLocationPermissionExplanation()
        }
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun showBackgroundLocationPermissionExplanation() {
        AlertDialog.Builder(this)
            .setTitle("Background Location Permission")
            .setMessage("This app requires background location access even when the app is closed to provide timely notifications and alert you if you're at risk of being late.")
            .setPositiveButton("Allow") { _, _ ->
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    locationCode
                )
            }
            .setNegativeButton("Deny") { _, _ -> /* Optional: Handle deny action */ }
            .create()
            .show()
    }
}

