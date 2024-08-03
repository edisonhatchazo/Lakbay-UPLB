package com.example.classschedule.algorithm.osrms


import com.example.classschedule.BuildConfig
import com.example.classschedule.algorithm.retrofit.RetrofitClient
import com.example.classschedule.algorithm.transit.BusRoute
import com.example.classschedule.algorithm.transit.BusStop
import com.example.classschedule.algorithm.transit.ParkingSpot
import com.example.classschedule.algorithm.transit.RouteWithLineString
import com.example.classschedule.algorithm.transit.calculateDoubleTransitRoute
import com.example.classschedule.algorithm.transit.calculateSingleTransitRoute
import com.example.classschedule.algorithm.transit.findNearestParkingSpot
import org.maplibre.android.geometry.LatLng


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
        minimumWalkingDistance: Int,
        doubleTransit: Boolean,
        libraryPoint: LatLng,
        upGatePoint: LatLng
    ): List<RouteWithLineString> {
        return when (profile) {
            "driving" -> listOf(RouteWithLineString(drivingService.getRoute(profile, "$start;$end"), "#FF0000", ""))
            "bicycle" -> listOf(RouteWithLineString(cyclingService.getRoute(profile, "$start;$end"), "#FFA500", ""))
            "foot" -> listOf(RouteWithLineString(walkingService.getRoute(profile, "$start;$end"), "#0000FF", ""))
            "transit" -> if (doubleTransit) {
                calculateDoubleTransitRoute(
                    start.split(",")[1].toDouble(),
                    start.split(",")[0].toDouble(),
                    end.split(",")[1].toDouble(),
                    end.split(",")[0].toDouble(),
                    libraryPoint,
                    upGatePoint,
                    busStopsKaliwa,
                    busStopsKanan,
                    busStopsForestry,
                    walkingService,
                    drivingService,
                    busRoutes,
                    minimumWalkingDistance
                )
            } else {
                calculateSingleTransitRoute(
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
            }
            else -> throw IllegalArgumentException("Unknown profile: $profile")
        }
    }

    suspend fun getRouteWithParking(
        profile: String,
        start: String,
        end: String,
        parkingSpots: List<ParkingSpot>,
        radius: Double // Default radius to find the nearest parking spot
    ): List<RouteWithLineString> {
        val destination = LatLng(end.split(",")[1].toDouble(), end.split(",")[0].toDouble())
        val nearestParkingSpot = findNearestParkingSpot(destination, parkingSpots.map { LatLng(it.latitude, it.longitude) }, radius)

        return nearestParkingSpot?.let { parkingSpot ->
            val parkingCoordinates = "${parkingSpot.longitude},${parkingSpot.latitude}"
            val carOrCyclingRoute = when (profile) {
                "driving" -> RouteWithLineString(drivingService.getRoute(profile, "$start;$parkingCoordinates"), "#FF0000", "")
                "bicycle" -> RouteWithLineString(cyclingService.getRoute(profile, "$start;$parkingCoordinates"), "#FFA500", "")
                else -> throw IllegalArgumentException("Invalid profile for parking: $profile")
            }
            val walkingRoute = RouteWithLineString(walkingService.getRoute("foot", "$parkingCoordinates;$end"), "#0000FF", "")
            listOf(carOrCyclingRoute, walkingRoute)
        } ?: run {
            // Fallback to normal route if no parking spot is found within the radius
            when (profile) {
                "driving" -> listOf(RouteWithLineString(drivingService.getRoute(profile, "$start;$end"), "#FF0000", ""))
                "bicycle" -> listOf(RouteWithLineString(cyclingService.getRoute(profile, "$start;$end"), "#FFA500", ""))
                else -> throw IllegalArgumentException("Invalid profile for parking: $profile")
            }
        }
    }
}
