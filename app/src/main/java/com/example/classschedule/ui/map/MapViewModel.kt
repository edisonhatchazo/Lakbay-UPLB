package com.example.classschedule.ui.map

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.classschedule.algorithm.osrms.OSRMRepository
import com.example.classschedule.algorithm.transit.BusStop
import com.example.classschedule.algorithm.transit.ParkingSpot
import com.example.classschedule.algorithm.transit.RouteWithLineString
import com.example.classschedule.algorithm.transit.loadAllBusStops
import com.example.classschedule.algorithm.transit.loadAllParkingSpots
import com.example.classschedule.ui.settings.global.RouteSettingsViewModel
import kotlinx.coroutines.launch
import org.maplibre.android.geometry.LatLng

class MapViewModel(private val repository: OSRMRepository, application: Application) : AndroidViewModel(application) {

    private val _routeResponse = MutableLiveData<List<RouteWithLineString>>()
    val routeResponse: LiveData<List<RouteWithLineString>> = _routeResponse
    var isCalculatingRoute = false
    private val _busStopsKaliwa = MutableLiveData<List<BusStop>>()
    private val _busStopsKanan = MutableLiveData<List<BusStop>>()
    private val _busStopsForestry = MutableLiveData<List<BusStop>>()
    private val _parkingSpots = MutableLiveData<List<ParkingSpot>>()

    private val _initialLocation = MutableLiveData<LatLng>()
    val initialLocation: LiveData<LatLng> = _initialLocation

    private val _destinationLocation = MutableLiveData<LatLng>()

    private val _selectedRouteType = MutableLiveData<String>()

    private val routeSettingsViewModel = RouteSettingsViewModel(application)


    init {
        loadAllBusStops()
        loadAllParkingSpots()
    }

    fun loadAllBusStops() {
        viewModelScope.launch {
            val (kaliwaStops, kananStops, forestryStops) = loadAllBusStops(getApplication())
            _busStopsKaliwa.postValue(kaliwaStops)
            _busStopsKanan.postValue(kananStops)
            _busStopsForestry.postValue(forestryStops)
        }
    }

    fun loadAllParkingSpots() {
        viewModelScope.launch {
            val parkingSpots = loadAllParkingSpots(getApplication())
            _parkingSpots.postValue(parkingSpots)
        }
    }

    private fun fetchRoute(profile: String, start: String, end: String) {
        viewModelScope.launch {
            val minimumWalkingDistance = routeSettingsViewModel.walkingDistance.value
            val doubleTransit = routeSettingsViewModel.forestryRouteDoubleRideEnabled.value
            val routes = repository.getRoute(
                profile,
                start,
                end,
                _busStopsKaliwa.value ?: emptyList(),
                _busStopsKanan.value ?: emptyList(),
                _busStopsForestry.value ?: emptyList(),
                minimumWalkingDistance,
                doubleTransit,
                libraryPoint =  LatLng(14.16570083526473, 121.23851448662828),
                upGatePoint =  LatLng(14.167623176682682, 121.24295209618953)

            )
            _routeResponse.postValue(routes)
        }
    }

    fun calculateRouteFromUserInput(profile: String, startLat: Double, startLng: Double, endLat: Double, endLng: Double) {

        viewModelScope.launch {
            val radius = routeSettingsViewModel.parkingRadius.value
            isCalculatingRoute = true
            val start = "$startLng,$startLat"
            val end = "$endLng,$endLat"
            if (profile == "driving" || profile == "bicycle") {
                try {
                    val routes = repository.getRouteWithParking(profile, start, end, _parkingSpots.value ?: emptyList(),radius)
                    _routeResponse.postValue(routes)
                } catch (e: IllegalArgumentException) {
                    // Handle case where no parking spot is found within the radius
                    Log.d("No Parking","No parking spot found within the specified radius.")
                }
            } else {
                fetchRoute(profile, start, end)
            }
            isCalculatingRoute = false
        }
    }

    fun updateInitialLocation(newLocation: LatLng) {
        _initialLocation.value = newLocation
        _selectedRouteType.value?.let { routeType ->
            _destinationLocation.value?.let { destination ->
                calculateRouteFromUserInput(
                    routeType,
                    newLocation.latitude,
                    newLocation.longitude,
                    destination.latitude,
                    destination.longitude
                )
            }
        }
    }
}

