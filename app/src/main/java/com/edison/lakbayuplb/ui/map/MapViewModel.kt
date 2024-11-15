package com.edison.lakbayuplb.ui.map

import android.app.Application
import androidx.compose.runtime.saveable.Saver
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.edison.lakbayuplb.algorithm.routing_algorithm.LocalRoutingRepository
import com.edison.lakbayuplb.algorithm.routing_algorithm.RouteWithLineString
import com.edison.lakbayuplb.algorithm.routing_algorithm.transit.BusStop
import com.edison.lakbayuplb.algorithm.routing_algorithm.transit.ParkingSpot
import com.edison.lakbayuplb.algorithm.routing_algorithm.transit.calculateDoubleTransitRoute
import com.edison.lakbayuplb.algorithm.routing_algorithm.transit.calculateSingleTransitRoute
import com.edison.lakbayuplb.algorithm.routing_algorithm.transit.loadAllBusStops
import com.edison.lakbayuplb.algorithm.routing_algorithm.transit.loadAllParkingSpots
import com.edison.lakbayuplb.ui.settings.global.RouteSettingsViewModel
import kotlinx.coroutines.launch
import org.maplibre.android.geometry.LatLng
import org.osmdroid.util.GeoPoint

class MapViewModel(private val repository: LocalRoutingRepository, application: Application) : AndroidViewModel(application) {

    private val _routeResponse =
        MutableLiveData<MutableList<Pair<String, MutableList<Pair<String, MutableList<RouteWithLineString>>>>>>()
    val routeResponse: LiveData<MutableList<Pair<String, MutableList<Pair<String, MutableList<RouteWithLineString>>>>>> =
        _routeResponse


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
        val context = getApplication<Application>().applicationContext
        viewModelScope.launch {
            val start = "$startLng,$startLat"
            val end = "$endLng,$endLat"

            // Initialize `routeResponse` to the new structure
            val routeResponse: MutableList<Pair<String, MutableList<Pair<String, MutableList<RouteWithLineString>>>>> = when (profile) {
                "foot" -> {
                    // Foot profile: only one walking route
                    val walkingRoute = repository.getRoute(
                        context,
                        "foot",
                        start,
                        end,
                        "#00FF00" // Green for walking
                    ).toMutableList()
                    mutableListOf("foot" to mutableListOf("foot" to walkingRoute))
                }
                "driving", "bicycle" -> {
                    // Car or Bicycle profile
                    val (mainRoute, walkToDestination) = repository.getRouteWithParking(
                        context,
                        profile,
                        start,
                        end,
                        colorCode, // Red for car, blue for bicycle
                        _parkingSpots.value ?: emptyList(),
                        routeSettingsViewModel.parkingRadius.value
                    )
                    mutableListOf(
                        profile to mutableListOf(profile to mainRoute.toMutableList()),          // First: Car or bicycle route
                        "foot" to mutableListOf("foot" to walkToDestination.toMutableList())    // Second: Walking route to destination
                    )
                }
                "transit" -> {
                    if (!doubleTransit) {
                        calculateSingleTransitRoute(
                            context,
                            startLat,
                            startLng,
                            endLat,
                            endLng,
                            _busStopsKaliwa.value ?: emptyList(),
                            _busStopsKanan.value ?: emptyList(),
                            _busStopsForestry.value ?: emptyList(),
                            busRoutes,
                            routeSettingsViewModel.walkingDistance.value,
                            repository
                        )
                    } else {
                        calculateDoubleTransitRoute(
                            context,
                            startLat,
                            startLng,
                            endLat,
                            endLng,
                            libraryPoint = GeoPoint(14.16570083526473, 121.23851448662828),
                            upGatePoint = GeoPoint(14.167623176682682, 121.24295209618953),
                            _busStopsKaliwa.value ?: emptyList(),
                            _busStopsKanan.value ?: emptyList(),
                            _busStopsForestry.value ?: emptyList(),
                            busRoutes,
                            routeSettingsViewModel.walkingDistance.value,
                            repository
                        )
                    }
                }

                else -> mutableListOf()
            }

            // Update `_routeResponse` with the newly formatted response
            _routeResponse.postValue(routeResponse)
            isCalculatingRoute = false
        }
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




// Custom Saver for GeoPoint to ensure it can be saved and restored properly
fun geoPointSaver() = Saver<GeoPoint?, List<Double>>(
    save = { geoPoint ->
        geoPoint?.let { listOf(it.latitude, it.longitude) } ?: emptyList()
    },
    restore = { list ->
        if (list.isNotEmpty()) GeoPoint(list[0], list[1]) else null
    }
)