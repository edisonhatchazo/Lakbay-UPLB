package com.edison.lakbayuplb.ui.settings.global

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edison.lakbayuplb.algorithm.notifications.cancelAllClassAlarms
import com.edison.lakbayuplb.algorithm.notifications.cancelAllExamAlarms
import com.edison.lakbayuplb.algorithm.notifications.scheduleClassAlarms
import com.edison.lakbayuplb.algorithm.notifications.scheduleExamAlarms
import com.edison.lakbayuplb.algorithm.notifications.scheduleWeeklyExamSummary
import com.edison.lakbayuplb.data.AppDataContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class RouteSettingsViewModel(context: Context) : ViewModel() {
    private val speedPreferences = SpeedPreferences(context)
    private val appPreferences = AppPreferences(context)
    private val _walkingSpeed = MutableStateFlow(speedPreferences.walkingSpeed) // meters per second
    val walkingSpeed: StateFlow<Double> = _walkingSpeed.asStateFlow()

    private val _slowestWalkingSpeed = MutableStateFlow(0.1) // 0.1 meters per second
    val slowestWalkingSpeed: StateFlow<Double> = _slowestWalkingSpeed.asStateFlow()

    private val _cyclingSpeed = MutableStateFlow(speedPreferences.cyclingSpeed / 3.6) // km/h converted to m/s
    val cyclingSpeed: StateFlow<Double> = _cyclingSpeed.asStateFlow()

    private val _carSpeed = MutableStateFlow(20.0 /3.6) // 20 km/h converted to m/s
    val carSpeed: StateFlow<Double> = _carSpeed.asStateFlow()

    private val _jeepneySpeed = MutableStateFlow(10.0 /3.6) // 10 km/h converted to m/s
    val jeepneySpeed: StateFlow<Double> = _jeepneySpeed.asStateFlow()

    private val _minimumWalkingDistance = MutableStateFlow(speedPreferences.minimumWalkingDistance) // 600 meters
    val walkingDistance: StateFlow<Int> = _minimumWalkingDistance.asStateFlow()

    private val _forestryRouteDoubleRideEnabled = MutableStateFlow(speedPreferences.forestryRouteDoubleRideEnabled)
    val forestryRouteDoubleRideEnabled: StateFlow<Boolean> = _forestryRouteDoubleRideEnabled.asStateFlow()

    private val _parkingRadius = MutableStateFlow(speedPreferences.parkingRadius)
    val parkingRadius: StateFlow<Double> = _parkingRadius.asStateFlow()

    private val _classNotificationEnabled = MutableStateFlow(appPreferences.isClassesNotificationEnabled())
    val classNotificationEnabled: StateFlow<Boolean> = _classNotificationEnabled.asStateFlow()

    private val _navigationNotificationEnabled = MutableStateFlow(appPreferences.isNavigationNotificationEnabled())
    val navigationNotificationEnabled: StateFlow<Boolean> = _navigationNotificationEnabled.asStateFlow()

    private val _examNotificationEnabled = MutableStateFlow(appPreferences.isExamsNotificationEnabled())
    val examNotificationEnabled: StateFlow<Boolean> = _examNotificationEnabled.asStateFlow()

    private val _speechEnabled = MutableStateFlow(appPreferences.isNavigationSpeechEnabled())
    val speechEnabled: StateFlow<Boolean> = _speechEnabled.asStateFlow()


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
    fun toggleClassNotificationEnabled(context: Context,isEnabled: Boolean) {
        appPreferences.setClassesNotification(isEnabled)
        _classNotificationEnabled.value = isEnabled

        if (isEnabled) {
            viewModelScope.launch {
                val classSchedules = AppDataContainer(context).classScheduleRepository.getAllClassSchedules().first()
                scheduleClassAlarms(context, classSchedules, AppDataContainer(context).buildingRepository, delay = 60)
            }
        } else {
            viewModelScope.launch {
                cancelAllClassAlarms(context)
            }
        }
    }

    fun toggleExamNotificationEnabled(context:Context,isEnabled: Boolean) {
        appPreferences.setExamsNotification(isEnabled)
        _examNotificationEnabled.value = isEnabled

        if (isEnabled) {
            viewModelScope.launch {
                val examSchedules = AppDataContainer(context).examScheduleRepository.getAllExamsSchedules().first()
                scheduleExamAlarms(context, examSchedules, AppDataContainer(context).buildingRepository, delay = 60)
                scheduleWeeklyExamSummary(context)
            }
        } else {
            viewModelScope.launch {
                cancelAllExamAlarms(context)
            }
        }
    }

    fun toggleNavigationNotificationEnabled(isEnabled: Boolean) {
        appPreferences.setNavigationNotification(isEnabled) // Save preference
        _navigationNotificationEnabled.value = isEnabled // Update LiveData or StateFlow
    }


    fun toggleNavigationSpeechEnabled(isEnabled: Boolean) {
        appPreferences.setNavigationSpeech(isEnabled) // Save preference
        _speechEnabled.value = isEnabled // Update LiveData or StateFlow
    }

}

