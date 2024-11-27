package com.edison.lakbayuplb.ui.map

import android.app.Application
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
        doubleTransit: Boolean
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
                        end
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
                        _parkingSpots.value ?: emptyList(),
                        routeSettingsViewModel.parkingRadius.value
                    )
                    mutableListOf(
                        profile to mutableListOf(profile to mainRoute.toMutableList()),
                        "foot" to mutableListOf("foot" to walkToDestination.toMutableList())
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
                            context = context,
                            startLat = startLat,
                            startLon = startLng,
                            endLat = endLat,
                            endLon = endLng,
                            libraryPoint = GeoPoint(14.16570083526473, 121.23851448662828),
                            upGatePoint = GeoPoint(14.167623176682682, 121.24295209618953),
                            busStopsKaliwa = _busStopsKaliwa.value ?: emptyList(),
                            busStopsKanan = _busStopsKanan.value ?: emptyList(),
                            busStopsForestry = _busStopsForestry.value ?: emptyList(),
                            busRoutes = busRoutes,
                            minimumWalkingDistance = routeSettingsViewModel.walkingDistance.value,
                            repository = repository
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