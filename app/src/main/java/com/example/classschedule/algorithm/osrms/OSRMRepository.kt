package com.example.classschedule.algorithm.osrms

import com.example.classschedule.algorithm.retrofit.RetrofitClient
import com.example.classschedule.algorithm.transit.BusRoute
import com.example.classschedule.algorithm.transit.BusStop
import com.example.classschedule.algorithm.transit.calculateTransitRoute


class OSRMRepository(private val busRoutes: List<BusRoute>) {
    // Define the base URLs for each service

    private val drivingService: OSRMService = RetrofitClient.getClient("http://ec2-3-107-38-50.ap-southeast-2.compute.amazonaws.com:5001/").create(OSRMService::class.java)
    private val cyclingService: OSRMService = RetrofitClient.getClient("http://ec2-3-107-38-50.ap-southeast-2.compute.amazonaws.com:5003/").create(OSRMService::class.java)
    private val walkingService: OSRMService = RetrofitClient.getClient("http://ec2-3-107-38-50.ap-southeast-2.compute.amazonaws.com:5002/").create(OSRMService::class.java)

    suspend fun getRoute(
        profile: String,
        start: String,
        end: String,
        busStopsKaliwa: List<BusStop>,
        busStopsKanan: List<BusStop>,
        busStopsForestry: List<BusStop>
    ): List<Pair<RouteResponse, String>> {
        return when (profile) {
            "driving" -> listOf(drivingService.getRoute(profile, "$start;$end") to "#FF0000")
            "bicycle" -> listOf(cyclingService.getRoute(profile, "$start;$end") to "#FFA500")
            "foot" -> listOf(walkingService.getRoute(profile, "$start;$end") to "#0000FF")
            "transit" -> {
                val startLat = start.split(",")[1].toDouble()
                val startLon = start.split(",")[0].toDouble()
                val endLat = end.split(",")[1].toDouble()
                val endLon = end.split(",")[0].toDouble()

                calculateTransitRoute(
                    startLat, startLon, endLat, endLon,
                    busStopsKaliwa, busStopsKanan, busStopsForestry,
                    walkingService, drivingService, busRoutes
                )
            }
            else -> throw IllegalArgumentException("Unknown profile: $profile")
        }
    }
}