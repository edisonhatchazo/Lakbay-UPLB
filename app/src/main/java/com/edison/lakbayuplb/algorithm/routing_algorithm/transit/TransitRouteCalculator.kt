package com.edison.lakbayuplb.algorithm.routing_algorithm.transit

import android.content.Context
import com.edison.lakbayuplb.algorithm.routing_algorithm.LocalRoutingRepository
import com.edison.lakbayuplb.algorithm.routing_algorithm.RouteWithLineString
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.osmdroid.util.GeoPoint

suspend fun calculateDoubleTransitRoute(
    context: Context,
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
    repository: LocalRoutingRepository
): MutableList<Pair<String, MutableList<Pair<String, MutableList<RouteWithLineString>>>>> {

    // Define waiting time penalties
    val libraryWaitingTime = 120.0
    val upGateWaitingTime = 60.0

    // Sequentially calculate the paths
    val pathA = calculateSingleTransitRoute(context, startLat, startLon, libraryPoint.latitude, libraryPoint.longitude, busStopsKaliwa, busStopsKanan, busStopsForestry, busRoutes, minimumWalkingDistance, repository)
    val pathB = calculateSingleTransitRoute(context, startLat, startLon, upGatePoint.latitude, upGatePoint.longitude, busStopsKaliwa, busStopsKanan, busStopsForestry, busRoutes, minimumWalkingDistance, repository)
    val pathC = calculateSingleTransitRoute(context, libraryPoint.latitude, libraryPoint.longitude, endLat, endLon, busStopsKaliwa, busStopsKanan, busStopsForestry, busRoutes, minimumWalkingDistance, repository)
    val pathD = calculateSingleTransitRoute(context, upGatePoint.latitude, upGatePoint.longitude, endLat, endLon, busStopsKaliwa, busStopsKanan, busStopsForestry, busRoutes, minimumWalkingDistance, repository)
    val pathE = calculateSingleTransitRoute(context, startLat, startLon, endLat, endLon, busStopsKaliwa, busStopsKanan, busStopsForestry, busRoutes, minimumWalkingDistance, repository)

    // Utility function to calculate total path duration
    fun calculateTotalDuration(
        path: MutableList<Pair<String, MutableList<Pair<String, MutableList<RouteWithLineString>>>>>
    ): Double {
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
    return when {
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
    context: Context,
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
): MutableList<Pair<String, MutableList<Pair<String, MutableList<RouteWithLineString>>>>> = coroutineScope{

    val kaliwaBusStops = loadBusStops(context,"shortKaliwa.geojson")
    val kananBusStops = loadBusStops(context,"shortKanan.geojson")
    val start = "$startLon,$startLat"
    val end = "$endLon,$endLat"

    // Check if walking alone is sufficient
    val walkingRoute = repository.getRoute(context,"foot", start, end)
    val walkingDistance = walkingRoute.sumOf { it.route.distance }
    if (walkingDistance <= minimumWalkingDistance) {
        return@coroutineScope mutableListOf("foot" to mutableListOf("foot" to walkingRoute)) // Only a walking route needed
    }

    // Concurrently find nearest bus stops
    val (nearestKaliwaStartBusStops, nearestKananStartBusStops, nearestForestryStartBusStops) = awaitAll(
        async { findNearestBusStops(busStopsKaliwa, startLat, startLon, 3) },
        async { findNearestBusStops(busStopsKanan, startLat, startLon, 3) },
        async { findNearestBusStops(busStopsForestry, startLat, startLon, 3) }
    )

    val (nearestKaliwaEndBusStops, nearestKananEndBusStops, nearestForestryEndBusStops) = awaitAll(
        async { findNearestBusStops(busStopsKaliwa, endLat, endLon, 3) },
        async { findNearestBusStops(busStopsKanan, endLat, endLon, 3) },
        async { findNearestBusStops(busStopsForestry, endLat, endLon, 3) }
    )

    val shortKaliwaStartBusStops = findNearestBusStops(kaliwaBusStops, startLat, startLon, 3)
    val shortKananStartBusStops = findNearestBusStops(kananBusStops, startLat, startLon, 3)

    val shortKaliwaEndBusStops = findNearestBusStops(kaliwaBusStops, endLat, endLon, 3)
    val shortKananEndBusStops = findNearestBusStops(kananBusStops, endLat, endLon, 3)

    val insideRoute = calculateOutsideRoute(
        context = context,
        startLat = startLat,
        startLon = startLon,
        endLat = endLat,
        endLon = endLon,
        kaliwaStartBusStops = shortKaliwaStartBusStops,
        kaliwaEndBusStops = shortKaliwaEndBusStops,
        kananStartBusStops = shortKananStartBusStops,
        kananEndBusStops = shortKananEndBusStops,
        repository = repository,
        busRoutes = busRoutes
    )

    // Concurrently calculate routes for each type
    val (kaliwaRoutes, kananRoutes, forestryRoutes) = awaitAll(
        async {
            calculateRoutesForType(
                startBusStops = nearestKaliwaStartBusStops,
                endBusStops = nearestKaliwaEndBusStops,
                busRoutes = busRoutes.filter { it.name.startsWith("Kaliwa") },
                repository = repository,
                context = context,
                startLat = startLat,
                startLon = startLon,
                endLat = endLat,
                endLon = endLon,
                routeTypePrefix = "Kaliwa"
            )
        },
        async {
            calculateRoutesForType(
                startBusStops = nearestKananStartBusStops,
                endBusStops = nearestKananEndBusStops,
                busRoutes = busRoutes.filter { it.name.startsWith("Kanan") },
                repository = repository,
                context = context,
                startLat = startLat,
                startLon = startLon,
                endLat = endLat,
                endLon = endLon,
                routeTypePrefix = "Kanan"
            )
        },
        async {
            calculateRoutesForType(
                startBusStops = nearestForestryStartBusStops,
                endBusStops = nearestForestryEndBusStops,
                busRoutes = busRoutes.filter { it.name.startsWith("Forestry") },
                repository = repository,
                context = context,
                startLat = startLat,
                startLon = startLon,
                endLat = endLat,
                endLon = endLon,
                routeTypePrefix = "Forestry"
            )
        }
    )
    val combinedArray = kaliwaRoutes + kananRoutes + forestryRoutes + insideRoute

    var sortedCombinedArray = combinedArray.sortedBy { route ->
        route.first // Sorting by the first value of the Triple (total duration)
    }

    val mostOptimalRoute = sortedCombinedArray.firstOrNull()

    if (mostOptimalRoute != null) {
        sortedCombinedArray = if (mostOptimalRoute.second == "Forestry Route Up" || mostOptimalRoute.second == "Forestry Route Down") {
            // Keep only the most optimal forestry route
            mutableListOf(mostOptimalRoute)
        } else {
            // Remove all forestry routes except the most optimal one
            sortedCombinedArray.filterNot { route ->
                route.second == "Forestry Route Up" || route.second == "Forestry Route Down"
            }.toMutableList()
        }
    }


    val initialFoot = sortedCombinedArray.firstOrNull()?.third?.getOrNull(0)?.second?.let {
        mutableListOf("foot" to it)
    } ?: mutableListOf()

    val finalFoot = sortedCombinedArray.firstOrNull()?.third?.getOrNull(2)?.second?.let {
        mutableListOf("foot" to it)
    } ?: mutableListOf()

    val combinedTransitSegments = mutableListOf<Pair<String, MutableList<RouteWithLineString>>>()
    sortedCombinedArray.forEach { route ->
        val transitSegment = route.third.getOrNull(1)?.second
        if (transitSegment != null) {
            combinedTransitSegments.add("transit" to transitSegment)
        }
    }
    return@coroutineScope mutableListOf(
        "foot" to initialFoot,
        "transit" to combinedTransitSegments,
        "foot" to finalFoot
    )

}

suspend fun calculateRoutesForType(
    context: Context,
    startBusStops: List<BusStop>,
    endBusStops: List<BusStop>,
    busRoutes: List<BusRoute>,
    repository: LocalRoutingRepository,
    startLat: Double,
    startLon: Double,
    endLat: Double,
    endLon: Double,
    routeTypePrefix: String
): MutableList<Triple<Double,String, MutableList<Pair<String, MutableList<RouteWithLineString>>>>> = coroutineScope {

    val start = "$startLon,$startLat"
    val end = "$endLon,$endLat"
    val routeResults = mutableListOf<Triple<Double,String, MutableList<Pair<String, MutableList<RouteWithLineString>>>>>()
    val optimalResult = mutableListOf<Pair<String,MutableList<RouteWithLineString>>>()
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
                        context,
                        "foot",
                        start,
                        "${startBusStop.lon},${startBusStop.lat}"
                    )
                    val routeCoordinates = findRouteCoordinates(startBusStop, endBusStop, busRoutes)
                    val transitRoute = repository.getRouteWithPredefinedPath(
                        context,
                        "transit",
                        routeCoordinates.coordinates
                    )
                    // Calculate walking route from end bus stop to destination
                    val walkingRouteToEnd = repository.getRoute(
                        context,
                        "foot",
                        "${endBusStop.lon},${endBusStop.lat}",
                        end
                    )

                    // Calculate total time for this route
                    val totalWalkingTime = walkingRouteToBusStop.sumOf { it.route.duration } + walkingRouteToEnd.sumOf { it.route.duration }
                    val totalTransitTime = transitRoute.route.duration
                    val totalTime = totalWalkingTime + totalTransitTime

                    // Update optimal route if this route is the shortest
                    if (totalTime < shortestTime) {
                        val mutableList = mutableListOf<RouteWithLineString>()
                        shortestTime = totalTime
                        optimalInitialFoot = walkingRouteToBusStop.toMutableList()
                        mutableList.add(transitRoute)
                        optimalTransit = mutableList.toMutableList()
                        optimalEndFoot = walkingRouteToEnd.toMutableList()
                    }
                }
            }
        }

        if (optimalInitialFoot != null && optimalTransit != null && optimalEndFoot != null) {
            optimalResult.add("foot" to optimalInitialFoot)
            optimalResult.add("transit" to optimalTransit)
            optimalResult.add("foot" to optimalEndFoot)
            routeResults.add(Triple(shortestTime, busRoute.name, optimalResult))

        }
    }

    return@coroutineScope routeResults
}

suspend fun calculateOutsideRoute(
    context: Context,
    kaliwaStartBusStops: List<BusStop>,
    kaliwaEndBusStops: List<BusStop>,
    kananStartBusStops: List<BusStop>,
    kananEndBusStops: List<BusStop>,
    busRoutes: List<BusRoute>,
    repository: LocalRoutingRepository,
    startLat: Double,
    startLon: Double,
    endLat: Double,
    endLon: Double
): MutableList<Triple<Double, String, MutableList<Pair<String, MutableList<RouteWithLineString>>>>> = coroutineScope {

    val start = "$startLon,$startLat"
    val end = "$endLon,$endLat"

    val routeResults = mutableListOf<Triple<Double, String, MutableList<Pair<String, MutableList<RouteWithLineString>>>>>()
    val optimalResult = mutableListOf<Pair<String,MutableList<RouteWithLineString>>>()

    val filteredKaliwaRoutes = busRoutes.filter { it.name == "Kaliwa Route 1" }
    val filteredKananRoutes = busRoutes.filter { it.name == "Kanan Route 1" }

    var shortestTime = Double.MAX_VALUE
    var optimalInitialFoot: MutableList<RouteWithLineString> ?= null
    var optimalTransit: MutableList<RouteWithLineString> ?= null
    var optimalEndFoot: MutableList<RouteWithLineString> ?= null

    // Calculate routes for "Kaliwa Route 1"
    for (busRoute in filteredKaliwaRoutes) {
        for (startBusStop in kaliwaStartBusStops) {
            for (endBusStop in kaliwaEndBusStops) {
                val startIndex = busRoute.coordinates.indexOf(GeoPoint(startBusStop.lat, startBusStop.lon))
                val endIndex = busRoute.coordinates.indexOf(GeoPoint(endBusStop.lat, endBusStop.lon))

                if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
                    // Calculate walking route to start bus stop
                    val walkingRouteToBusStop = repository.getRoute(
                        context,
                        "foot",
                        start,
                        "${startBusStop.lon},${startBusStop.lat}"
                    )
                    val routeCoordinates = findRouteCoordinates(startBusStop,endBusStop,busRoutes)
                    val transitRoute = repository.getRouteWithPredefinedPath(
                        context,
                        "transit",
                        routeCoordinates.coordinates
                    )
                    val walkingRouteToEnd = repository.getRoute(
                        context,
                        "foot",
                        "${endBusStop.lon},${endBusStop.lat}",
                        end
                    )

                    // Calculate total time
                    val totalWalkingTime = walkingRouteToBusStop.sumOf { it.route.duration } +
                            walkingRouteToEnd.sumOf { it.route.duration }
                    val totalTransitTime = transitRoute.route.duration
                    val totalTime = totalWalkingTime + totalTransitTime

                    // Update if this route is more optimal
                    if (totalTime < shortestTime) {
                        val mutableList = mutableListOf<RouteWithLineString>()
                        shortestTime = totalTime

                        optimalInitialFoot = walkingRouteToBusStop.toMutableList()
                        mutableList.add(transitRoute)
                        optimalTransit = mutableList.toMutableList()
                        optimalEndFoot = walkingRouteToEnd.toMutableList()

                    }
                }
            }
        }
    }
    if (optimalInitialFoot != null && optimalTransit != null && optimalEndFoot != null) {
        optimalResult.add("foot" to optimalInitialFoot)
        optimalResult.add("transit" to optimalTransit)
        optimalResult.add("foot" to optimalEndFoot)
        routeResults.add(Triple(shortestTime, "Kaliwa Route 1", optimalResult.toMutableList()))
    }
    optimalResult.clear()
    shortestTime = Double.MAX_VALUE

    for (busRoute in filteredKananRoutes) {
        for (startBusStop in kananStartBusStops) {
            for (endBusStop in kananEndBusStops) {
                val startIndex = busRoute.coordinates.indexOf(GeoPoint(startBusStop.lat, startBusStop.lon))
                val endIndex = busRoute.coordinates.indexOf(GeoPoint(endBusStop.lat, endBusStop.lon))
                if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
                    // Calculate walking route to start bus stop
                    val walkingRouteToBusStop = repository.getRoute(
                        context,
                        "foot",
                        start,
                        "${startBusStop.lon},${startBusStop.lat}"
                    )
                    val routeCoordinates = findRouteCoordinates(startBusStop,endBusStop,busRoutes)
                    val transitRoute = repository.getRouteWithPredefinedPath(
                        context,
                        "transit",
                        routeCoordinates.coordinates
                    )
                    val walkingRouteToEnd = repository.getRoute(
                        context,
                        "foot",
                        "${endBusStop.lon},${endBusStop.lat}",
                        end
                    )

                    val totalWalkingTime = walkingRouteToBusStop.sumOf { it.route.duration } +
                            walkingRouteToEnd.sumOf { it.route.duration }
                    val totalTransitTime = transitRoute.route.duration
                    val totalTime = totalWalkingTime + totalTransitTime

                    if (totalTime < shortestTime) {
                        val mutableList = mutableListOf<RouteWithLineString>()
                        shortestTime = totalTime
                        optimalInitialFoot = walkingRouteToBusStop.toMutableList()
                        mutableList.add(transitRoute)
                        optimalTransit = mutableList.toMutableList()
                        optimalEndFoot = walkingRouteToEnd.toMutableList()

                    }
                }
            }
        }
    }

    if (optimalInitialFoot != null && optimalTransit != null && optimalEndFoot != null) {
        optimalResult.add("foot" to optimalInitialFoot)
        optimalResult.add("transit" to optimalTransit)
        optimalResult.add("foot" to optimalEndFoot)
        routeResults.add(Triple(shortestTime, "Kanan Route 1", optimalResult.toMutableList()))
    }

    return@coroutineScope routeResults.sortedBy { it.first }.toMutableList()
}
