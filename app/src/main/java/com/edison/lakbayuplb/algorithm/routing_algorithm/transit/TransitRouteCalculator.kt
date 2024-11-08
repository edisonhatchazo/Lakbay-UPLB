package com.edison.lakbayuplb.algorithm.routing_algorithm.transit

import com.edison.lakbayuplb.algorithm.routing_algorithm.LocalRoutingRepository
import com.edison.lakbayuplb.algorithm.routing_algorithm.RouteWithLineString
import com.edison.lakbayuplb.ui.settings.global.RouteSettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.osmdroid.util.GeoPoint

@OptIn(ExperimentalCoroutinesApi::class)
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
): MutableList<Pair<String, MutableList<Pair<String, MutableList<RouteWithLineString>>>>> = coroutineScope {

    // Define waiting time penalties
    val libraryWaitingTime = 120.0
    val upGateWaitingTime = 60.0

    // Determine the number of threads based on available cores
    val coreCount = Runtime.getRuntime().availableProcessors()
    val threadCount = when {
        coreCount > 4 -> 5
        coreCount == 4 -> 4
        coreCount == 2 -> 2
        else -> 1
    }
    val dispatcher = Dispatchers.IO.limitedParallelism(threadCount)

    // Launch path calculations on different threads
    val (pathA, pathB, pathC, pathD, pathE) = withContext(dispatcher) {
        listOf(
            async { calculateSingleTransitRoute(startLat, startLon, libraryPoint.latitude, libraryPoint.longitude, busStopsKaliwa, busStopsKanan, busStopsForestry, busRoutes, minimumWalkingDistance, repository, routeSettingsViewModel) },
            async { calculateSingleTransitRoute(startLat, startLon, upGatePoint.latitude, upGatePoint.longitude, busStopsKaliwa, busStopsKanan, busStopsForestry, busRoutes, minimumWalkingDistance, repository, routeSettingsViewModel) },
            async { calculateSingleTransitRoute(libraryPoint.latitude, libraryPoint.longitude, endLat, endLon, busStopsKaliwa, busStopsKanan, busStopsForestry, busRoutes, minimumWalkingDistance, repository, routeSettingsViewModel) },
            async { calculateSingleTransitRoute(upGatePoint.latitude, upGatePoint.longitude, endLat, endLon, busStopsKaliwa, busStopsKanan, busStopsForestry, busRoutes, minimumWalkingDistance, repository, routeSettingsViewModel) },
            async { calculateSingleTransitRoute(startLat, startLon, endLat, endLon, busStopsKaliwa, busStopsKanan, busStopsForestry, busRoutes, minimumWalkingDistance, repository, routeSettingsViewModel) }
        ).awaitAll()
    }

    // Utility function to calculate total path duration
    fun calculateTotalDuration(
        path: MutableList<Pair<String, MutableList<Pair<String, MutableList<RouteWithLineString>>>>>
    ): Double {
        // Calculate walking duration and distance, using the first available RouteWithLineString for each foot segment
        val walkingDuration = path.filter { it.first == "foot" }.sumOf { outerPair ->
            outerPair.second.sumOf { innerPair ->
                val routeWithLineString = innerPair.second.firstOrNull()
                routeWithLineString?.route?.duration ?: 0.0
            }
        }
        val transitDuration = path.filter { it.first == "transit" }.sumOf { outerPair ->
            outerPair.second.sumOf { innerPair ->
                val routeWithLineString = innerPair.second.firstOrNull()
                routeWithLineString?.route?.duration ?: 0.0
            }
        }
        return walkingDuration + transitDuration
    }

    // Calculate total durations with penalties
    val totalDurationAC = calculateTotalDuration(pathA) + calculateTotalDuration(pathC) + libraryWaitingTime
    val totalDurationBD = calculateTotalDuration(pathB) + calculateTotalDuration(pathD) + upGateWaitingTime
    val durationE = calculateTotalDuration(pathE)

    // Determine the optimal route and concatenate while preserving structure
    return@coroutineScope when {
        durationE < totalDurationAC && durationE < totalDurationBD -> pathE // Return single route pathE as it’s optimal
        totalDurationAC < totalDurationBD -> {
            // Combine pathA and pathC
            val combinedPath = mutableListOf<Pair<String, MutableList<Pair<String, MutableList<RouteWithLineString>>>>>()
            combinedPath.addAll(pathA.dropLast(1)) // Add pathA without its last segment to avoid duplication
            combinedPath.addAll(pathC)
            combinedPath
        }
        else -> {
            // Combine pathB and pathD
            val combinedPath = mutableListOf<Pair<String, MutableList<Pair<String, MutableList<RouteWithLineString>>>>>()
            combinedPath.addAll(pathB.dropLast(1)) // Add pathB without its last segment to avoid duplication
            combinedPath.addAll(pathD)
            combinedPath
        }
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
): MutableList<Pair<String, MutableList<Pair<String, MutableList<RouteWithLineString>>>>> {

    val start = "$startLon,$startLat"
    val end = "$endLon,$endLat"

    // Check if walking alone is sufficient
    val walkingRoute = repository.getRoute("foot", start, end, "#00FF00", routeSettingsViewModel)
    val walkingDistance = walkingRoute.sumOf { it.route.distance }
    if (walkingDistance <= minimumWalkingDistance) {
        return mutableListOf("foot" to mutableListOf("foot" to walkingRoute)) // Only a walking route needed
    }

    // Find nearest bus stops for each route type
    val nearestKaliwaStartBusStops = findNearestBusStops(busStopsKaliwa, startLat, startLon, 3)
    val nearestKananStartBusStops = findNearestBusStops(busStopsKanan, startLat, startLon, 3)
    val nearestForestryStartBusStops = findNearestBusStops(busStopsForestry, startLat, startLon, 3)

    val nearestKaliwaEndBusStops = findNearestBusStops(busStopsKaliwa, endLat, endLon, 3)
    val nearestKananEndBusStops = findNearestBusStops(busStopsKanan, endLat, endLon, 3)
    val nearestForestryEndBusStops = findNearestBusStops(busStopsForestry, endLat, endLon, 3)

    // Calculate routes for each type
    val kaliwaRoutes = calculateRoutesForType(
        startBusStops = nearestKaliwaStartBusStops,
        endBusStops = nearestKaliwaEndBusStops,
        busRoutes = busRoutes.filter { it.name.startsWith("Kaliwa") },
        repository = repository,
        routeSettingsViewModel = routeSettingsViewModel,
        startLat = startLat,
        startLon = startLon,
        endLat = endLat,
        endLon = endLon,
        routeTypePrefix = "Kaliwa"
    )

    val kananRoutes = calculateRoutesForType(
        startBusStops = nearestKananStartBusStops,
        endBusStops = nearestKananEndBusStops,
        busRoutes = busRoutes.filter { it.name.startsWith("Kanan") },
        repository = repository,
        routeSettingsViewModel = routeSettingsViewModel,
        startLat = startLat,
        startLon = startLon,
        endLat = endLat,
        endLon = endLon,
        routeTypePrefix = "Kanan"
    )

    val forestryRoutes = calculateRoutesForType(
        startBusStops = nearestForestryStartBusStops,
        endBusStops = nearestForestryEndBusStops,
        busRoutes = busRoutes.filter { it.name.startsWith("Forestry") },
        repository = repository,
        routeSettingsViewModel = routeSettingsViewModel,
        startLat = startLat,
        startLon = startLon,
        endLat = endLat,
        endLon = endLon,
        routeTypePrefix = "Forestry"
    )

    val routeGroups = mutableListOf<MutableList<Pair<String, MutableList<RouteWithLineString>>>>()
    val combinedArray = kaliwaRoutes + kananRoutes + forestryRoutes

    for (i in combinedArray.indices step 3) {
        val list = mutableListOf<Pair<String, MutableList<RouteWithLineString>>>()
        list.add(combinedArray[i].second.first())
        list.add(combinedArray[i + 1].second.first())
        list.add(combinedArray[i + 2].second.first())
        routeGroups.add(list)
    }

    val sortedRouteGroups = routeGroups.sortedBy { routeGroup ->
        routeGroup.sumOf { segmentPair ->
            segmentPair.second.sumOf { routeSegment -> routeSegment.route.duration }
        }
    }

    val initialFoot = mutableListOf(
        ("foot" to sortedRouteGroups.firstOrNull()?.getOrNull(0)!!.second)
    )

    val finalFoot = mutableListOf(
        ("foot" to sortedRouteGroups.firstOrNull()?.getOrNull(2)!!.second)
    )

    val combinedTransitSegments = mutableListOf<Pair<String, MutableList<RouteWithLineString>>>()
    sortedRouteGroups.forEach { routeGroup ->
        val transitSegment = routeGroup.getOrNull(1)?.second
        if (transitSegment != null) {
            combinedTransitSegments.add(routeGroup[1].first to transitSegment)
        }
    }
    return mutableListOf(
        "foot" to initialFoot,
        "transit" to combinedTransitSegments,
        "foot" to finalFoot
    )


}

suspend fun calculateRoutesForType(
    startBusStops: List<BusStop>,
    endBusStops: List<BusStop>,
    busRoutes: List<BusRoute>,
    repository: LocalRoutingRepository,
    routeSettingsViewModel: RouteSettingsViewModel,
    startLat: Double,
    startLon: Double,
    endLat: Double,
    endLon: Double,
    routeTypePrefix: String
): MutableList<Pair<String, MutableList<Pair<String, MutableList<RouteWithLineString>>>>> = coroutineScope {

    val start = "$startLon,$startLat"
    val end = "$endLon,$endLat"
    val routeResults = mutableListOf<Pair<String, MutableList<Pair<String, MutableList<RouteWithLineString>>>>>()

    // Filter busRoutes to include only those matching the specified routeTypePrefix
    val filteredRoutes = busRoutes.filter { it.name.startsWith(routeTypePrefix) }
    var shortestTime = Double.MAX_VALUE
    var optimalInitialFoot: MutableList<RouteWithLineString>? = null
    var optimalTransit: MutableList<RouteWithLineString>? = null
    var optimalEndFoot: MutableList<RouteWithLineString>? = null

    for (busRoute in filteredRoutes) {
        for (startBusStop in startBusStops) {
            for (endBusStop in endBusStops) {
                val startIndex = busRoute.coordinates.indexOf(GeoPoint(startBusStop.lat, startBusStop.lon))
                val endIndex = busRoute.coordinates.indexOf(GeoPoint(endBusStop.lat, endBusStop.lon))

                if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
                    // Calculate walking route to start bus stop
                    val walkingRouteToBusStop = repository.getRoute(
                        "foot",
                        start,
                        "${startBusStop.lon},${startBusStop.lat}",
                        "#00FF00",
                        routeSettingsViewModel
                    )

                    // Calculate transit route along the predefined path
                    val routeCoordinates = findRouteCoordinates(startBusStop, endBusStop, busRoutes)
                    val transitRoute = repository.getRouteWithPredefinedPath(
                        "transit",
                        routeCoordinates.coordinates,
                        "#FF0000",
                        routeSettingsViewModel
                    )

                    // Calculate walking route from end bus stop to destination
                    val walkingRouteToEnd = repository.getRoute(
                        "foot",
                        "${endBusStop.lon},${endBusStop.lat}",
                        end,
                        "#00FF00",
                        routeSettingsViewModel
                    )

                    // Calculate total time for this route
                    val totalWalkingTime = walkingRouteToBusStop.sumOf { it.route.duration } + walkingRouteToEnd.sumOf { it.route.duration }
                    val totalTransitTime = transitRoute.sumOf { it.route.duration }
                    val totalTime = totalWalkingTime + totalTransitTime

                    // Update optimal route if this route is the shortest
                    if (totalTime < shortestTime) {
                        shortestTime = totalTime
                        optimalInitialFoot = walkingRouteToBusStop.toMutableList()
                        optimalTransit = transitRoute.toMutableList()
                        optimalEndFoot = walkingRouteToEnd.toMutableList()
                    }
                }
            }
        }

        if (optimalInitialFoot != null && optimalTransit != null && optimalEndFoot != null) {
            routeResults.add("foot" to mutableListOf("foot" to optimalInitialFoot))
            routeResults.add("transit" to mutableListOf(busRoute.name to optimalTransit))
            routeResults.add("foot" to mutableListOf("foot" to optimalEndFoot))
        }
    }

    return@coroutineScope routeResults
}
