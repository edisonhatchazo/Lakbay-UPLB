package com.example.classschedule.ui.map

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.classschedule.algorithm.osrms.OSRMRepository
import com.example.classschedule.algorithm.osrms.RouteResponse
import com.example.classschedule.algorithm.transit.BusStop
import com.example.classschedule.algorithm.transit.loadAllBusStops
import com.example.classschedule.ui.settings.global.RouteSettingsViewModel
import kotlinx.coroutines.launch
import org.maplibre.android.geometry.LatLng

class MapViewModel(private val repository: OSRMRepository, application: Application) : AndroidViewModel(application) {

    private val _routeResponse = MutableLiveData<List<Pair<RouteResponse, String>>>()
    val routeResponse: LiveData<List<Pair<RouteResponse, String>>> = _routeResponse

    private val _busStopsKaliwa = MutableLiveData<List<BusStop>>()
    private val _busStopsKanan = MutableLiveData<List<BusStop>>()
    private val _busStopsForestry = MutableLiveData<List<BusStop>>()

    private val _initialLocation = MutableLiveData<LatLng>()
    val initialLocation: LiveData<LatLng> = _initialLocation

    private val _destinationLocation = MutableLiveData<LatLng>()
    val destinationLocation: LiveData<LatLng> = _destinationLocation

    private val _selectedRouteType = MutableLiveData<String>()
    val selectedRouteType: LiveData<String> = _selectedRouteType

    private val routeSettingsViewModel = RouteSettingsViewModel(application)

    init {
        loadAllBusStops()
    }

    fun loadAllBusStops() {
        viewModelScope.launch {
            val (kaliwaStops, kananStops, forestryStops) = loadAllBusStops(getApplication())
            _busStopsKaliwa.postValue(kaliwaStops)
            _busStopsKanan.postValue(kananStops)
            _busStopsForestry.postValue(forestryStops)
        }
    }

    private fun fetchRoute(profile: String, start: String, end: String) {
        viewModelScope.launch {
            val minimumWalkingDistance = routeSettingsViewModel.walkingDistance.value
            val routes = repository.getRoute(
                profile,
                start,
                end,
                _busStopsKaliwa.value ?: emptyList(),
                _busStopsKanan.value ?: emptyList(),
                _busStopsForestry.value ?: emptyList(),
                minimumWalkingDistance
            )
            _routeResponse.postValue(routes)
        }
    }

    fun calculateRouteFromUserInput(profile: String, startLat: Double, startLng: Double, endLat: Double, endLng: Double) {
        fetchRoute(profile, "$startLng,$startLat", "$endLng,$endLat")
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

    fun updateRouteType(routeType: String) {
        _selectedRouteType.value = routeType
    }

    fun updateDestinationLocation(destination: LatLng) {
        _destinationLocation.value = destination
    }
}

