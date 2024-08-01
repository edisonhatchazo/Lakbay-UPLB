package com.example.classschedule.algorithm.osrms


import com.example.classschedule.BuildConfig
import com.example.classschedule.algorithm.retrofit.RetrofitClient
import com.example.classschedule.algorithm.transit.BusRoute
import com.example.classschedule.algorithm.transit.BusStop
import com.example.classschedule.algorithm.transit.calculateTransitRoute

class OSRMRepository(
    private val busRoutes: List<BusRoute>,
) {
    private val drivingService: OSRMService = RetrofitClient.getClient(BuildConfig.DRIVING_API_BASE_URL).create(OSRMService::class.java)
    private val walkingService: OSRMService = RetrofitClient.getClient(BuildConfig.WALKING_API_BASE_URL).create(OSRMService::class.java)
    private val cyclingService: OSRMService = RetrofitClient.getClient(BuildConfig.CYCLING_API_BASE_URL).create(OSRMService::class.java)

    suspend fun getRoute(
        profile: String,
        start: String,
        end: String,
        busStopsKaliwa: List<BusStop>,
        busStopsKanan: List<BusStop>,
        busStopsForestry: List<BusStop>,
        minimumWalkingDistance: Int
    ): List<Pair<RouteResponse, String>> {
        return when (profile) {
            "driving" -> listOf(drivingService.getRoute(profile, "$start;$end") to "#FF0000")
            "bicycle" -> listOf(cyclingService.getRoute(profile, "$start;$end") to "#FFA500")
            "foot" -> listOf(walkingService.getRoute(profile, "$start;$end") to "#0000FF")
            "transit" -> calculateTransitRoute(
                start.split(",")[1].toDouble(),
                start.split(",")[0].toDouble(),
                end.split(",")[1].toDouble(),
                end.split(",")[0].toDouble(),
                busStopsKaliwa,
                busStopsKanan,
                busStopsForestry,
                walkingService,
                drivingService,
                busRoutes,
                minimumWalkingDistance
            )
            else -> throw IllegalArgumentException("Unknown profile: $profile")
        }
    }
}
