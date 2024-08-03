package com.example.classschedule.algorithm.transit

import android.util.Log
import com.example.classschedule.algorithm.osrms.OSRMService
import com.example.classschedule.algorithm.osrms.RouteResponse
import kotlinx.coroutines.coroutineScope
import org.maplibre.android.geometry.LatLng

data class RouteWithLineString(
    val routeResponse: RouteResponse,
    val colorCode: String,
    val lineString: String
)
suspend fun calculateDoubleTransitRoute(
    startLat: Double,
    startLon: Double,
    endLat: Double,
    endLon: Double,
    libraryPoint: LatLng,
    upGatePoint: LatLng,
    busStopsKaliwa: List<BusStop>,
    busStopsKanan: List<BusStop>,
    busStopsForestry: List<BusStop>,
    osrmFootService: OSRMService,
    osrmCarService: OSRMService,
    busRoutes: List<BusRoute>,
    minimumWalkingDistance: Int
): List<RouteWithLineString> = coroutineScope {

    val nearestLibraryBusStops = findNearestBusStops(busStopsKaliwa + busStopsKanan + busStopsForestry, libraryPoint.latitude, libraryPoint.longitude, 1)
    val nearestUPGateBusStops = findNearestBusStops(busStopsKaliwa + busStopsKanan + busStopsForestry, upGatePoint.latitude, upGatePoint.longitude, 1)

    val nearestLibraryBusStop = nearestLibraryBusStops.first()
    val nearestUPGateBusStop = nearestUPGateBusStops.first()

    val libraryWaitingTime = 120.0
    val upGateWaitingTime = 60.0



    Log.d("Time Duration","Path A")
    // Calculate paths A and B
    var pathA = calculateSingleTransitRoute(
        startLat, startLon,
        nearestLibraryBusStop.lat, nearestLibraryBusStop.lon,
        busStopsKaliwa, busStopsKanan, busStopsForestry,
        osrmFootService, osrmCarService, busRoutes, minimumWalkingDistance
    )
    Log.d("Time Duration","Path B")
    var pathB = calculateSingleTransitRoute(
        startLat, startLon,
        nearestUPGateBusStop.lat, nearestUPGateBusStop.lon,
        busStopsKaliwa, busStopsKanan, busStopsForestry,
        osrmFootService, osrmCarService, busRoutes, minimumWalkingDistance
    )
    Log.d("Time Duration","Path C")
    // Calculate paths C and D
    var pathC = calculateSingleTransitRoute(
        libraryPoint.latitude, libraryPoint.longitude,
        endLat, endLon,
        busStopsKaliwa, busStopsKanan, busStopsForestry,
        osrmFootService, osrmCarService, busRoutes, minimumWalkingDistance
    )

    Log.d("Time Duration","Path D")
    var pathD = calculateSingleTransitRoute(
        upGatePoint.latitude, upGatePoint.longitude,
        endLat, endLon,
        busStopsKaliwa, busStopsKanan, busStopsForestry,
        osrmFootService, osrmCarService, busRoutes, minimumWalkingDistance
    )
    Log.d("Time Duration", "Path E")
    val pathE = calculateSingleTransitRoute(
        startLat, startLon,
        endLat, endLon,
        busStopsKaliwa, busStopsKanan, busStopsForestry,
        osrmFootService, osrmCarService, busRoutes, minimumWalkingDistance
    )
    // Combine paths C+A and D+B and calculate total durations
    val totalDurationAC = pathA.sumOf { it.routeResponse.routes.first().duration } + pathC.sumOf { it.routeResponse.routes.first().duration  } + libraryWaitingTime
    val totalDurationBD = pathB.sumOf { it.routeResponse.routes.first().duration } + pathD.sumOf { it.routeResponse.routes.first().duration } + upGateWaitingTime
    val durationE = pathE.sumOf{it.routeResponse.routes.first().duration}
    // Return the route with the shortest total duration

    if (totalDurationAC < totalDurationBD) {
        if (pathA.isNotEmpty()) pathA = pathA.dropLast(1)
        if(durationE < totalDurationAC)
            return@coroutineScope pathE
        else
            return@coroutineScope pathA + pathC
    } else {
        if (pathB.isNotEmpty()) pathB = pathB.dropLast(1)
        if(durationE<totalDurationBD)
            return@coroutineScope pathE
        else
            return@coroutineScope pathB + pathD
    }
}

suspend fun calculateSingleTransitRoute(
    startLat: Double,
    startLon: Double,
    endLat: Double,
    endLon: Double,
    busStopsKaliwa: List<BusStop>,
    busStopsKanan: List<BusStop>,
    busStopsForestry: List<BusStop>,
    osrmFootService: OSRMService,
    osrmCarService: OSRMService,
    busRoutes: List<BusRoute>,
    minimumWalkingDistance: Int
): List<RouteWithLineString> = coroutineScope {
    val walkingRoute = osrmFootService.getRoute(
        profile = "foot",
        coordinates = "$startLon,$startLat;$endLon,$endLat"
    )
    val walkingDistance = walkingRoute.routes[0].distance

    if (walkingDistance <= minimumWalkingDistance) {
        return@coroutineScope listOf(RouteWithLineString(walkingRoute, "#0000FF", ""))
    }

    val nearestKaliwaStartBusStops = findNearestBusStops(busStopsKaliwa, startLat, startLon, 3)
    val nearestKananStartBusStops = findNearestBusStops(busStopsKanan, startLat, startLon, 3)
    val nearestForestryStartBusStops = findNearestBusStops(busStopsForestry, startLat, startLon, 2)

    val nearestKaliwaEndBusStops = findNearestBusStops(busStopsKaliwa, endLat, endLon, 3)
    val nearestKananEndBusStops = findNearestBusStops(busStopsKanan, endLat, endLon, 3)
    val nearestForestryEndBusStops = findNearestBusStops(busStopsForestry, endLat, endLon, 2)

    val allStartBusStops = nearestKaliwaStartBusStops + nearestKananStartBusStops + nearestForestryStartBusStops
    val allEndBusStops = nearestKaliwaEndBusStops + nearestKananEndBusStops + nearestForestryEndBusStops

    var optimalRoute: List<RouteWithLineString>? = null
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

                if (startIndex < endIndex) {
                    val walkingRouteToBusStop = osrmFootService.getRoute(
                        profile = "foot",
                        coordinates = "$startLon,$startLat;${startBusStop.lon},${startBusStop.lat}"
                    )

                    val routeCoordinates = findRouteCoordinates(startBusStop, endBusStop, listOf(route))
                    val transitRoute = osrmCarService.getRoute(
                        profile = "driving",
                        coordinates = routeCoordinates.coordinates
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
                            RouteWithLineString(walkingRouteToBusStop, "#0000FF", ""),
                            RouteWithLineString(transitRoute, "#00FF00", routeCoordinates.routeName),
                            RouteWithLineString(walkingRouteToEnd, "#0000FF", "")
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