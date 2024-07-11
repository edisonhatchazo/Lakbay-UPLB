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
import kotlinx.coroutines.launch

class MapViewModel(private val repository: OSRMRepository, application: Application) : AndroidViewModel(application) {

    private val _routeResponse = MutableLiveData<List<Pair<RouteResponse, String>>>()
    val routeResponse: LiveData<List<Pair<RouteResponse, String>>> = _routeResponse

    private val _busStopsKaliwa = MutableLiveData<List<BusStop>>()
    private val _busStopsKanan = MutableLiveData<List<BusStop>>()
    private val _busStopsForestry = MutableLiveData<List<BusStop>>()
    init{
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
            val routes: List<Pair<RouteResponse, String>> = when (profile) {
                "driving", "bicycle", "foot" -> {
                    val response = repository.getRoute(profile, start, end, emptyList(), emptyList(), emptyList())
                    response.map { it.first to when (profile) {
                        "foot" -> "#0000FF"
                        "bicycle" -> "#FFA500"
                        "driving" -> "#FF0000"
                        else -> "#000000"
                    }}
                }
                "transit" -> {
                    val busStopsKaliwa = _busStopsKaliwa.value ?: emptyList()
                    val busStopsKanan = _busStopsKanan.value ?: emptyList()
                    val busStopsForestry = _busStopsForestry.value ?: emptyList()
                    repository.getRoute(profile, start, end, busStopsKaliwa, busStopsKanan, busStopsForestry)
                }
                else -> throw IllegalArgumentException("Unknown profile: $profile")
            }
            _routeResponse.postValue(routes)
        }
    }

    fun calculateRouteFromUserInput(profile: String, startLat: Double, startLng: Double, endLat: Double, endLng: Double) {
       fetchRoute(profile, "$startLng,$startLat", "$endLng,$endLat")
    }

}


