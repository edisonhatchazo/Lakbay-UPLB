package com.edison.lakbayuplb.algorithm.routing_algorithm.transit

import android.util.Log
import com.edison.lakbayuplb.algorithm.routing_algorithm.LocalRoutingRepository
import com.edison.lakbayuplb.algorithm.routing_algorithm.RouteWithLineString
import com.edison.lakbayuplb.ui.settings.global.RouteSettingsViewModel
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import org.osmdroid.util.GeoPoint
import java.util.concurrent.Executors


suspend fun calculateDoubleTransitRoute(
    startLat: Double,
    startLon: Double,
    endLat: Double,
    endLon: Double,
    libraryPoint: GeoPoint,
    upGatePoint: GeoPoint,
    busStopsKaliwa: List<BusStop>,
    busStopsKanan: List<BusStop>,
    busStopsForestry: List<BusStop>,
    busRoutes: List<BusRoute>,
    minimumWalkingDistance: Int,
    repository: LocalRoutingRepository,
    routeSettingsViewModel: RouteSettingsViewModel
): List<RouteWithLineString> = coroutineScope {

    val nearestLibraryBusStops = findNearestBusStops(busStopsKaliwa + busStopsKanan + busStopsForestry, libraryPoint.latitude, libraryPoint.longitude, 1)
    val nearestUPGateBusStops = findNearestBusStops(busStopsKaliwa + busStopsKanan + busStopsForestry, upGatePoint.latitude, upGatePoint.longitude, 1)

    val nearestLibraryBusStop = nearestLibraryBusStops.first()
    val nearestUPGateBusStop = nearestUPGateBusStops.first()

    // Waiting Time Penalties
    val libraryWaitingTime = 120.0
    val upGateWaitingTime = 60.0

    // Calculate paths A and B using A*
    var pathA = calculateSingleTransitRoute(
        startLat, startLon,
        nearestLibraryBusStop.lat, nearestLibraryBusStop.lon,
        busStopsKaliwa, busStopsKanan, busStopsForestry,
        busRoutes, minimumWalkingDistance,repository, routeSettingsViewModel
    )

    var pathB = calculateSingleTransitRoute(
        startLat, startLon,
        nearestUPGateBusStop.lat, nearestUPGateBusStop.lon,
        busStopsKaliwa, busStopsKanan, busStopsForestry,
        busRoutes, minimumWalkingDistance,repository, routeSettingsViewModel
    )

    // Calculate paths C and D
    val pathC = calculateSingleTransitRoute(
        libraryPoint.latitude, libraryPoint.longitude,
        endLat, endLon,
        busStopsKaliwa, busStopsKanan, busStopsForestry,
        busRoutes, minimumWalkingDistance,repository, routeSettingsViewModel
    )

    val pathD = calculateSingleTransitRoute(
        upGatePoint.latitude, upGatePoint.longitude,
        endLat, endLon,
        busStopsKaliwa, busStopsKanan, busStopsForestry,
        busRoutes, minimumWalkingDistance,repository, routeSettingsViewModel
    )

    val pathE = calculateSingleTransitRoute(
        startLat, startLon,
        endLat, endLon,
        busStopsKaliwa, busStopsKanan, busStopsForestry,
        busRoutes, minimumWalkingDistance,repository, routeSettingsViewModel
    )

    // Combine paths C+A and D+B and calculate total durations
    val totalDurationAC = pathA.sumOf { it.route.duration } + pathC.sumOf { it.route.duration } + libraryWaitingTime
    val totalDurationBD = pathB.sumOf { it.route.duration } + pathD.sumOf { it.route.duration } + upGateWaitingTime
    val durationE = pathE.sumOf { it.route.duration }

    // Return the route with the shortest total duration
    if (totalDurationAC < totalDurationBD) {
        if (pathA.isNotEmpty()) pathA = pathA.dropLast(1)
        return@coroutineScope if (durationE < totalDurationAC) pathE else pathA + pathC
    } else {
        if (pathB.isNotEmpty()) pathB = pathB.dropLast(1)
        return@coroutineScope if (durationE < totalDurationBD) pathE else pathB + pathD
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
    busRoutes: List<BusRoute>,
    minimumWalkingDistance: Int,
    repository: LocalRoutingRepository,
    routeSettingsViewModel: RouteSettingsViewModel
): List<RouteWithLineString> {
    val start = "$startLon,$startLat"
    val end = "$endLon,$endLat"

    val walkingRoute = repository.getRoute(
        "foot",
        start,
        end,
        "#00FF00",
        routeSettingsViewModel
    )
    val walkingDistance = walkingRoute.sumOf { it.route.distance }

    if (walkingDistance <= minimumWalkingDistance) {
        return walkingRoute
    }

    // Finding nearest bus stops (sequential)
    val nearestKaliwaStartBusStops = findNearestBusStops(busStopsKaliwa, startLat, startLon, 3)
    val nearestKananStartBusStops = findNearestBusStops(busStopsKanan, startLat, startLon, 3)
    val nearestForestryStartBusStops = findNearestBusStops(busStopsForestry, startLat, startLon, 3)

    val nearestKaliwaEndBusStops = findNearestBusStops(busStopsKaliwa, endLat, endLon, 3)
    val nearestKananEndBusStops = findNearestBusStops(busStopsKanan, endLat, endLon, 3)
    val nearestForestryEndBusStops = findNearestBusStops(busStopsForestry, endLat, endLon, 3)

    val allStartBusStops = nearestKaliwaStartBusStops + nearestKananStartBusStops + nearestForestryStartBusStops
    val allEndBusStops = nearestKaliwaEndBusStops + nearestKananEndBusStops + nearestForestryEndBusStops

    var optimalRoute: List<RouteWithLineString>? = null
    var shortestTime = Double.MAX_VALUE

    // Calculate number of cores and threads
    val availableCores = Runtime.getRuntime().availableProcessors()
    val numberOfThreads = availableCores * 3 // 3 threads per core

    val dispatcher = Executors.newFixedThreadPool(numberOfThreads).asCoroutineDispatcher()

    try {
        withTimeout(10_000_000) { // Set timeout to ensure no long-running tasks
            coroutineScope {
                val jobs = allStartBusStops.chunked(numberOfThreads).flatMap { chunkedStartBusStops ->
                    chunkedStartBusStops.map { startBusStop ->
                        launch(dispatcher) {
                            for (endBusStop in allEndBusStops) {
                                val route: BusRoute? = busRoutes.find { route ->
                                    route.coordinates.contains(GeoPoint(startBusStop.lat, startBusStop.lon)) &&
                                            route.coordinates.contains(GeoPoint(endBusStop.lat, endBusStop.lon))
                                }

                                if (route != null) {
                                    // Add back startIndex and endIndex logic
                                    val startIndex = route.coordinates.indexOf(GeoPoint(startBusStop.lat, startBusStop.lon))
                                    val endIndex = route.coordinates.indexOf(GeoPoint(endBusStop.lat, endBusStop.lon))

                                    if (startIndex < endIndex) {
                                        // Calculate walking route to bus stop
                                        val walkingRouteToBusStop = repository.getRoute(
                                            "foot",
                                            start,
                                            "${startBusStop.lon},${startBusStop.lat}",
                                            "#00FF00",
                                            routeSettingsViewModel
                                        )

                                        // Calculate transit route
                                        val routeCoordinates = findRouteCoordinates(startBusStop, endBusStop, busRoutes)
                                        val transitRoute = repository.getRouteWithPredefinedPath(
                                            "transit",
                                            routeCoordinates.coordinates, // Predefined coordinates from the bus route
                                            "#FF0000",
                                            routeSettingsViewModel
                                        )

                                        // Calculate walking route to the end destination
                                        val walkingRouteToEnd = repository.getRoute(
                                            "foot",
                                            "${endBusStop.lon},${endBusStop.lat}",
                                            end,
                                            "#00FF00",
                                            routeSettingsViewModel
                                        )

                                        // Total time calculation
                                        val totalWalkingTime = walkingRouteToBusStop.sumOf { it.route.duration } + walkingRouteToEnd.sumOf { it.route.duration }
                                        val totalTransitTime = transitRoute.sumOf { it.route.duration }
                                        val totalTime = totalWalkingTime + totalTransitTime
                                        // Update optimal route if this route is the shortest
                                        synchronized(this) {
                                            if (totalTime < shortestTime) {
                                                shortestTime = totalTime
                                                optimalRoute = walkingRouteToBusStop + transitRoute + walkingRouteToEnd

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Wait for all jobs to complete
                jobs.forEach { it.join() }
            }
        }
    } catch (e: TimeoutCancellationException) {
        Log.e("Map", "Timeout occurred while calculating routes.")
    } catch (e: Exception) {
        Log.e("Map", "Error occurred while calculating routes: ${e.message}")
    } finally {
        // Clean up dispatcher
        dispatcher.close()
    }

    if (optimalRoute == null) {
        throw IllegalArgumentException("No valid route found between bus stops")
    }

    return optimalRoute as List<RouteWithLineString>
}


fun findOptimalBusStop(
    nearestKaliwaBusStops: List<BusStop>,
    nearestKananBusStops: List<BusStop>,
    nearestForestryBusStops: List<BusStop>,
    userLat: Double,
    userLon: Double,
    distanceThreshold: Double // Threshold distance in meters
): BusStop {
    // Combine all the nearest bus stops from Kaliwa, Kanan, and Forestry
    val allNearestBusStops = nearestKaliwaBusStops + nearestKananBusStops + nearestForestryBusStops

    // Initialize variables to track the optimal bus stop or midpoint
    var optimalBusStop: BusStop? = null
    var shortestDistance = Double.MAX_VALUE

    // Compare all the nearest bus stops to find the optimal one
    for (busStop in allNearestBusStops) {
        // Calculate the distance from the user's location to this bus stop
        val distanceToUser = calculateDistance(userLat, userLon, busStop.lat, busStop.lon)

        // If the distance is less than the current shortest distance, update the optimal bus stop
        if (distanceToUser < shortestDistance) {
            shortestDistance = distanceToUser
            optimalBusStop = busStop
        }
    }


    for (startBusStop in allNearestBusStops) {
        for (endBusStop in allNearestBusStops) {
            // Avoid comparing the same bus stop
            if (startBusStop != endBusStop) {
                // Calculate the distance between these two bus stops
                val distanceBetweenBusStops = calculateDistance(startBusStop.lat, startBusStop.lon, endBusStop.lat, endBusStop.lon)

                // If the bus stops are closer than the threshold, calculate the midpoint
                if (distanceBetweenBusStops <= distanceThreshold) {
                    val midpointLatLon = calculateMidpoint(startBusStop, endBusStop)

                    // Create a temporary "midpoint" bus stop with a virtual name
                    val midpointBusStop = BusStop(
                        name = "Drop Off",
                        lat = midpointLatLon.first,
                        lon = midpointLatLon.second
                    )

                    // Calculate the distance from the user to the midpoint
                    val distanceToMidpoint = calculateDistance(userLat, userLon, midpointLatLon.first, midpointLatLon.second)

                    // Update the optimal bus stop if the midpoint is closer to the user than the current optimal stop
                    if (distanceToMidpoint < shortestDistance) {
                        shortestDistance = distanceToMidpoint
                        optimalBusStop = midpointBusStop
                    }
                }
            }
        }
    }

    return optimalBusStop ?: throw IllegalArgumentException("No valid bus stop found")
}

// Helper function: Calculate the midpoint between two bus stops
fun calculateMidpoint(startBusStop: BusStop, endBusStop: BusStop): Pair<Double, Double> {
    val midLat = (startBusStop.lat + endBusStop.lat) / 2
    val midLon = (startBusStop.lon + endBusStop.lon) / 2
    return Pair(midLat, midLon)
}