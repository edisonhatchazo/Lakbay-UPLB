package com.edison.lakbayuplb.ui.map

import android.app.Application
import android.util.Log
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.util.GeoPoint

class MapViewModel(private val repository: LocalRoutingRepository, application: Application) : AndroidViewModel(application) {

    private val _routeResponse = MutableLiveData(
        mutableListOf<Pair<String, MutableList<Pair<String, MutableList<RouteWithLineString>>>>>()
    )
    val routeResponse: LiveData<MutableList<Pair<String, MutableList<Pair<String, MutableList<RouteWithLineString>>>>>> =
        _routeResponse

    private val _busStopsKaliwa = MutableLiveData<List<BusStop>>()
    private val _busStopsKanan = MutableLiveData<List<BusStop>>()
    private val _shortBusStopsKaliwa = MutableLiveData<List<BusStop>>()
    private val _shortBusStopsKanan = MutableLiveData<List<BusStop>>()
    private val _busStopsForestry = MutableLiveData<List<BusStop>>()
    private val _parkingSpots = MutableLiveData<List<ParkingSpot>>()
    val busRoutes = repository.getBusRoutes()
    private val routeSettingsViewModel = RouteSettingsViewModel(application)

    val busStopsKaliwa =_busStopsKaliwa.value ?: emptyList()
    val busStopsKanan = _busStopsKanan.value ?: emptyList()
    val shortBusStopsKaliwa =_shortBusStopsKaliwa.value ?: emptyList()
    val shortBusStopsKanan = _shortBusStopsKanan.value ?: emptyList()
    val busStopsForestry = _busStopsForestry.value ?: emptyList()
    init {
        loadAllBusStops()
        loadAllParkingSpots()
        repository.initializeGraphs(context = application.baseContext)
    }
    private fun loadAllBusStops() {
        viewModelScope.launch {
            val busStops = loadAllBusStops(getApplication())
            _busStopsKaliwa.postValue(busStops.kaliwaStops)
            _busStopsKanan.postValue(busStops.kananStops)
            _busStopsForestry.postValue(busStops.forestryStops)
            _shortBusStopsKaliwa.postValue(busStops.shortKaliwaStops)
            _shortBusStopsKanan.postValue(busStops.shortKananStops)
        }
    }

    private fun loadAllParkingSpots() {
        viewModelScope.launch {
            val parkingSpots = loadAllParkingSpots(getApplication())
            _parkingSpots.postValue(parkingSpots)
        }
    }
    suspend fun calculateRouteFromUserInput(
        profile: String,
        startLat: Double,
        startLng: Double,
        endLat: Double,
        endLng: Double,
        doubleTransit: Boolean
    ): Boolean {
        val context = getApplication<Application>().applicationContext
        return withContext(Dispatchers.IO) {
            try {
                val start = "$startLng,$startLat"
                val end = "$endLng,$endLat"

                val routeResponse = when (profile) {
                    "foot" -> {
                        val walkingRoute = repository.getRoute(
                            context,
                            "foot",
                            start,
                            end
                        ).toMutableList()
                        mutableListOf("foot" to mutableListOf("foot" to walkingRoute))
                    }
                    "driving", "bicycle" -> {
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
                                context = context,
                                startLat = startLat,
                                startLon = startLng,
                                endLat = endLat,
                                endLon = endLng,
                                busStopsKaliwa = _busStopsKaliwa.value ?: emptyList(),
                                busStopsKanan = _busStopsKanan.value ?: emptyList(),
                                shortBusStopsKaliwa = _shortBusStopsKaliwa.value ?: emptyList(),
                                shortBusStopsKanan = _shortBusStopsKanan.value ?: emptyList(),
                                busStopsForestry = _busStopsForestry.value ?: emptyList(),
                                busRoutes = busRoutes,
                                minimumWalkingDistance = routeSettingsViewModel.walkingDistance.value,
                                repository = repository
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
                                shortBusStopsKaliwa = _shortBusStopsKaliwa.value ?: emptyList(),
                                shortBusStopsKanan = _shortBusStopsKanan.value ?: emptyList(),
                                busStopsForestry = _busStopsForestry.value ?: emptyList(),
                                busRoutes = busRoutes,
                                minimumWalkingDistance = routeSettingsViewModel.walkingDistance.value,
                                repository = repository
                            )
                        }
                    }
                    else -> mutableListOf()
                }

                _routeResponse.postValue(routeResponse)
                true // Calculation completed successfully
            } catch (e: Exception) {
                Log.e("RouteCalculation", "Error: ${e.message}")
                false // Indicate calculation failure
            }
        }
    }


}