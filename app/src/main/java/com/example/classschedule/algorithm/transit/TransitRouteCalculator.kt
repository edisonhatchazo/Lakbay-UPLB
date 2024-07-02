package com.example.classschedule.algorithm.transit

import com.example.classschedule.algorithm.osrms.OSRMService
import com.example.classschedule.algorithm.osrms.RouteResponse
import kotlinx.coroutines.coroutineScope
import org.maplibre.android.geometry.LatLng

suspend fun calculateTransitRoute(
    startLat: Double,
    startLon: Double,
    endLat: Double,
    endLon: Double,
    busStopsKaliwa: List<BusStop>,
    busStopsKanan: List<BusStop>,
    busStopsForestry: List<BusStop>,
    osrmFootService: OSRMService,
    osrmCarService: OSRMService,
    busRoutes: List<BusRoute>
): List<Pair<RouteResponse, String>> = coroutineScope {
    val nearestKaliwaStartBusStops = findNearestBusStops(busStopsKaliwa, startLat, startLon, 2)
    val nearestKananStartBusStops = findNearestBusStops(busStopsKanan, startLat, startLon, 2)
    val nearestForestryStartBusStops = findNearestBusStops(busStopsForestry, startLat, startLon, 2)

    val nearestKaliwaEndBusStops = findNearestBusStops(busStopsKaliwa, endLat, endLon, 2)
    val nearestKananEndBusStops = findNearestBusStops(busStopsKanan, endLat, endLon, 2)
    val nearestForestryEndBusStops = findNearestBusStops(busStopsForestry, endLat, endLon, 2)

    val allStartBusStops = nearestKaliwaStartBusStops + nearestKananStartBusStops + nearestForestryStartBusStops
    val allEndBusStops = nearestKaliwaEndBusStops + nearestKananEndBusStops + nearestForestryEndBusStops


    var optimalRoute: List<Pair<RouteResponse, String>>? = null
    var shortestTime = Double.MAX_VALUE
    for (startBusStop in allStartBusStops) {
        for (endBusStop in allEndBusStops) {

            val route = busRoutes.find {
                it.coordinates.contains(LatLng(startBusStop.lat, startBusStop.lon)) &&
                        it.coordinates.contains(LatLng(endBusStop.lat, endBusStop.lon))
            }
            if (route != null) {
                val startIndex = route.coordinates.indexOf(LatLng(startBusStop.lat, startBusStop.lon))
                val endIndex = route.coordinates.indexOf(LatLng(endBusStop.lat, endBusStop.lon))

                if(startIndex < endIndex) {
                    val walkingRouteToBusStop = osrmFootService.getRoute(
                        profile = "foot",
                        coordinates = "$startLon,$startLat;${startBusStop.lon},${startBusStop.lat}"
                    )

                    val transitRoute = osrmCarService.getRoute(
                        profile = "driving",
                        coordinates = findRouteCoordinates(startBusStop, endBusStop, listOf(route))
                    )

                    val walkingRouteToEnd = osrmFootService.getRoute(
                        profile = "foot",
                        coordinates = "${endBusStop.lon},${endBusStop.lat};$endLon,$endLat"
                    )

                    val totalWalkingTime =
                        walkingRouteToBusStop.routes[0].duration + walkingRouteToEnd.routes[0].duration
                    val totalTransitTime = transitRoute.routes[0].duration
                    val totalTime = totalWalkingTime + totalTransitTime

                    if (totalTime < shortestTime) {
                        shortestTime = totalTime
                        optimalRoute = listOf(
                            Pair(walkingRouteToBusStop, "#0000FF"), // Blue for walking
                            Pair(transitRoute, "#00FF00"), // Green for transit
                            Pair(walkingRouteToEnd, "#0000FF") // Blue for walking
                        )
                    }
                }
            }
        }
    }

    if (optimalRoute == null) {
        throw IllegalArgumentException("No valid route found between bus stops")
    }

    optimalRoute
}
