package com.edison.lakbayuplb.ui.map

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.edison.lakbayuplb.ui.map.routing_algorithm.LocalRoutingRepository
import com.edison.lakbayuplb.ui.map.routing_algorithm.RouteWithLineString
import com.edison.lakbayuplb.ui.map.routing_algorithm.transit.BusStop
import com.edison.lakbayuplb.ui.map.routing_algorithm.transit.ParkingSpot
import com.edison.lakbayuplb.ui.map.routing_algorithm.transit.calculateDoubleTransitRoute
import com.edison.lakbayuplb.ui.map.routing_algorithm.transit.calculateSingleTransitRoute
import com.edison.lakbayuplb.ui.map.routing_algorithm.transit.loadAllBusStops
import com.edison.lakbayuplb.ui.map.routing_algorithm.transit.loadAllParkingSpots
import com.edison.lakbayuplb.ui.settings.global.RouteSettingsViewModel
import kotlinx.coroutines.launch
import org.maplibre.android.geometry.LatLng
import org.osmdroid.util.GeoPoint

class MapViewModel(private val repository: LocalRoutingRepository, application: Application) : AndroidViewModel(application) {

    private val _routeResponse = MutableLiveData<List<RouteWithLineString>>()
    val routeResponse: LiveData<List<RouteWithLineString>> = _routeResponse
    var isCalculatingRoute = false
    private val _busStopsKaliwa = MutableLiveData<List<BusStop>>()
    private val _busStopsKanan = MutableLiveData<List<BusStop>>()
    private val _busStopsForestry = MutableLiveData<List<BusStop>>()
    private val _parkingSpots = MutableLiveData<List<ParkingSpot>>()
    private val busRoutes = repository.getBusRoutes()
    private val _initialLocation = MutableLiveData<LatLng>()
    val initialLocation: LiveData<LatLng> = _initialLocation

    private val _destinationLocation = MutableLiveData<LatLng>()

    private val _selectedRouteType = MutableLiveData<String>()

    private val routeSettingsViewModel = RouteSettingsViewModel(application)


    init {
        loadAllBusStops()
        loadAllParkingSpots()
        repository.initializeGraphs(context = application.baseContext)
    }

    private fun loadAllBusStops() {
        viewModelScope.launch {
            val (kaliwaStops, kananStops, forestryStops) = loadAllBusStops(getApplication())
            _busStopsKaliwa.postValue(kaliwaStops)
            _busStopsKanan.postValue(kananStops)
            _busStopsForestry.postValue(forestryStops)
        }
    }

    private fun loadAllParkingSpots() {
        viewModelScope.launch {
            val parkingSpots = loadAllParkingSpots(getApplication())
            _parkingSpots.postValue(parkingSpots)
        }
    }

    fun calculateRouteFromUserInput(
        profile: String,
        startLat: Double,
        startLng: Double,
        endLat: Double,
        endLng: Double,
        doubleTransit: Boolean,
        colorCode: String,
    ) {
        isCalculatingRoute = true

        viewModelScope.launch {
            val start = "$startLng,$startLat"
            val end = "$endLng,$endLat"

            when (profile) {
                "driving", "bicycle" -> {
                    try {
                        val routes = repository.getRouteWithParking(
                            profile,
                            start,
                            end,
                            colorCode,
                            _parkingSpots.value ?: emptyList(),
                            routeSettingsViewModel.parkingRadius.value,
                            routeSettingsViewModel
                        )
                        _routeResponse.postValue(routes)
                    } catch (e: IllegalArgumentException) {
                        Log.d("No Parking", "No parking spot found within the specified radius.")
                    }
                }
                "transit" -> {
                    // Handle transit (combination of foot + bus routes)
                    val routes = if (doubleTransit) {
                        calculateDoubleTransitRoute(
                            startLat,
                            startLng,
                            endLat,
                            endLng,
                            libraryPoint =  GeoPoint(14.16570083526473, 121.23851448662828),
                            upGatePoint =  GeoPoint(14.167623176682682, 121.24295209618953),
                            _busStopsKaliwa.value ?: emptyList(),
                            _busStopsKanan.value ?: emptyList(),
                            _busStopsForestry.value ?: emptyList(),
                            busRoutes = busRoutes,
                            routeSettingsViewModel.walkingDistance.value,
                            repository,
                            routeSettingsViewModel
                        )
                    } else {
                        calculateSingleTransitRoute(
                            startLat,
                            startLng,
                            endLat,
                            endLng,
                            _busStopsKaliwa.value ?: emptyList(),
                            _busStopsKanan.value ?: emptyList(),
                            _busStopsForestry.value ?: emptyList(),
                            busRoutes = busRoutes,
                            routeSettingsViewModel.walkingDistance.value,
                            repository,
                            routeSettingsViewModel
                        )
                    }
                    _routeResponse.postValue(routes)
                }
                else -> {
                    val routes = repository.getRoute(
                        profile,
                        start,
                        end,
                        colorCode,
                        routeSettingsViewModel
                    )
                    _routeResponse.postValue(routes)
                }
            }

            isCalculatingRoute = false
        }
    }

//    fun updateInitialLocation(newLocation: GeoPoint) {
//        _initialLocation.value = newLocation
//        _selectedRouteType.value?.let { routeType ->
//            _destinationLocation.value?.let { destination ->
//                calculateRouteFromUserInput(
//                    routeType,
//                    newLocation.latitude,
//                    newLocation.longitude,
//                    destination.latitude,
//                    destination.longitude
//                )
//            }
//        }
//    }
}

