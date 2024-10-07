package com.edison.lakbayuplb.ui.settings.global

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.edit

class AppPreferences(context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    companion object {
        private const val TOP_APP_BAR_BACKGROUND_COLOR_KEY = "top_app_bar_background_color"
        private const val TOP_APP_BAR_FOREGROUND_COLOR_KEY = "top_app_bar_foreground_color"
        private const val PREVIOUS_COLOR_ID_KEY = "previous_color_id" // New key for storing previous color ID
    }

    // Save the selected background and foreground colors
    fun saveTopAppBarColors(backgroundColor: Color, foregroundColor: Color) {
        preferences.edit().putInt(TOP_APP_BAR_BACKGROUND_COLOR_KEY, backgroundColor.toArgb()).apply()
        preferences.edit().putInt(TOP_APP_BAR_FOREGROUND_COLOR_KEY, foregroundColor.toArgb()).apply()
    }

    // Retrieve both the saved background and foreground colors
    fun getTopAppBarColors(): Pair<Color, Color> {
        val backgroundColorInt = preferences.getInt(TOP_APP_BAR_BACKGROUND_COLOR_KEY, Color.Blue.toArgb())
        val foregroundColorInt = preferences.getInt(TOP_APP_BAR_FOREGROUND_COLOR_KEY, Color.White.toArgb())
        return Pair(Color(backgroundColorInt), Color(foregroundColorInt))
    }

    // Save the previous color ID
    fun savePreviousColorId(id: Int) {
        preferences.edit().putInt(PREVIOUS_COLOR_ID_KEY, id).apply()
    }

    // Retrieve the previous color ID (default to 1 if none is saved)
    fun getPreviousColorId(): Int {
        return preferences.getInt(PREVIOUS_COLOR_ID_KEY, 1)
    }
}



class SpeedPreferences(context: Context) {
    private val preferences = context.getSharedPreferences("speed_prefs", Context.MODE_PRIVATE)

    var walkingSpeed: Double
        get() = preferences.getFloat("walking_speed", 1.0f).toDouble()
        set(value) = preferences.edit { putFloat("walking_speed", value.toFloat()) }

    var cyclingSpeed: Double
        get() = preferences.getFloat("cycling_speed", 10.0f).toDouble()
        set(value) = preferences.edit { putFloat("cycling_speed", value.toFloat()) }

    var minimumWalkingDistance: Int
        get() = preferences.getInt("minimum_walking_distance", 600)
        set(value) = preferences.edit { putInt("minimum_walking_distance", value) }

    var forestryRouteDoubleRideEnabled: Boolean
        get() = preferences.getBoolean("forestry_route_double_ride_enabled", false)
        set(value) = preferences.edit { putBoolean("forestry_route_double_ride_enabled", value) }

    var parkingRadius: Double
        get() = preferences.getFloat("parking_radius",300.0f).toDouble()
        set(value) = preferences.edit{ putFloat("parking_radius",value.toFloat())}
}