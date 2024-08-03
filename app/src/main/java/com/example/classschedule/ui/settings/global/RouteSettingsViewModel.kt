package com.example.classschedule.ui.settings.global

import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RouteSettingsViewModel(context: Context) : ViewModel() {
    private val speedPreferences = SpeedPreferences(context)

    private val _walkingSpeed = MutableStateFlow(speedPreferences.walkingSpeed) // meters per second
    val walkingSpeed: StateFlow<Double> = _walkingSpeed.asStateFlow()

    private val _cyclingSpeed = MutableStateFlow(speedPreferences.cyclingSpeed / 3.6) // km/h converted to m/s
    val cyclingSpeed: StateFlow<Double> = _cyclingSpeed.asStateFlow()

    private val _carSpeed = MutableStateFlow(20.0 /3.6) // 20 km/h converted to m/s
    val carSpeed: StateFlow<Double> = _carSpeed.asStateFlow()

    private val _jeepneySpeed = MutableStateFlow(10.0 /3.6) // 20 km/h converted to m/s
    val jeepneySpeed: StateFlow<Double> = _jeepneySpeed.asStateFlow()

    private val _minimumWalkingDistance = MutableStateFlow(speedPreferences.minimumWalkingDistance) // 600 meters
    val walkingDistance: StateFlow<Int> = _minimumWalkingDistance.asStateFlow()

    private val _forestryRouteDoubleRideEnabled = MutableStateFlow(speedPreferences.forestryRouteDoubleRideEnabled)
    val forestryRouteDoubleRideEnabled: StateFlow<Boolean> = _forestryRouteDoubleRideEnabled.asStateFlow()

    private val _parkingRadius = MutableStateFlow(speedPreferences.parkingRadius)
    val parkingRadius: StateFlow<Double> = _parkingRadius.asStateFlow()

    fun setWalkingSpeed(speed: Double) {
        viewModelScope.launch {
            _walkingSpeed.emit(speed)
            speedPreferences.walkingSpeed = speed
        }
    }

    fun setCyclingSpeed(speed: Double) {
        viewModelScope.launch {
            _cyclingSpeed.emit(speed / 3.6) // Store in m/s
            speedPreferences.cyclingSpeed = speed * 3.6 // Convert m/s to km/h for storage
        }
    }

    fun setParkingRadius(radius: Double){
        viewModelScope.launch{
            _parkingRadius.emit(radius)
            speedPreferences.parkingRadius = radius
        }
    }

    fun setMinimumWalkingDistance(distance: Int) {
        viewModelScope.launch {
            _minimumWalkingDistance.emit(distance)
            speedPreferences.minimumWalkingDistance = distance
        }
    }

    fun setForestryRouteDoubleRideEnabled(enabled: Boolean) {
        viewModelScope.launch {
            _forestryRouteDoubleRideEnabled.emit(enabled)
            speedPreferences.forestryRouteDoubleRideEnabled = enabled
        }
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