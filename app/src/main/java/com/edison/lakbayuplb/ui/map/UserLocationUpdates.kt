package com.edison.lakbayuplb.ui.map

import android.content.Context
import android.util.Log
import com.edison.lakbayuplb.algorithm.routing_algorithm.calculateBearing
import com.edison.lakbayuplb.algorithm.routing_algorithm.getTurnInstruction
import com.edison.lakbayuplb.algorithm.routing_algorithm.haversineInMeters
import com.edison.lakbayuplb.algorithm.routing_algorithm.parseGeoJSONGeometry
import com.edison.lakbayuplb.algorithm.routing_algorithm.transit.BusStop
import com.edison.lakbayuplb.algorithm.routing_algorithm.transit.calculateRoutesForType
import com.edison.lakbayuplb.algorithm.routing_algorithm.transit.findNearestBusStops
import com.edison.lakbayuplb.data.AppDataContainer
import org.osmdroid.util.GeoPoint

fun createGeoJsonLineString(coordinates: MutableList<Pair<Double, Double>>): String {

    val formattedCoordinates = coordinates.joinToString(",") { "[${it.second}, ${it.first}]" }

    return """{
        "type": "LineString",
        "coordinates": [$formattedCoordinates]
    }"""
}

fun getDestinations(
    lineString: MutableList<Pair<String, MutableList<MutableList<Pair<Double, Double>>>>>
): MutableList<MutableList<GeoPoint>> {
    val destinations = mutableListOf<MutableList<GeoPoint>>()

    lineString.forEach { profilePair ->
        val profileDestinations = profilePair.second.mapNotNull { points ->
            points.lastOrNull()?.let { GeoPoint(it.first, it.second) } // Add altitude if needed
        }.toMutableList()

        // Special handling for transit
        if (profilePair.first == "transit") {
            val transitDestinations = profileDestinations.take(10).distinct()
            destinations.add(transitDestinations.toMutableList())
        } else {
            destinations.add(profileDestinations)
        }
    }

    return destinations
}




fun isGeoPointInBounds(point: GeoPoint, polygon: List<GeoPoint>): Boolean {
    var inside = false
    val n = polygon.size

    if (n < 3) {
        return false // A polygon must have at least 3 points
    }

    var j = n - 1 // The last vertex is connected to the first
    for (i in 0 until n) {
        val lat1 = polygon[i].latitude
        val lon1 = polygon[i].longitude
        val lat2 = polygon[j].latitude
        val lon2 = polygon[j].longitude

        // Check if point is within the vertical bounds of the edge
        if ((lat1 > point.latitude) != (lat2 > point.latitude)) {
            val intersect = (point.latitude - lat1) * (lon2 - lon1) / (lat2 - lat1) + lon1
            if (point.longitude < intersect) {
                inside = !inside
            }
        }
        j = i
    }
    return inside
}
fun reduceToDirections(
    lineString: MutableList<Pair<String, MutableList<MutableList<Pair<Double, Double>>>>>
): MutableList<Pair<String, MutableList<Pair<String, GeoPoint>>>> {
    return lineString.map { profilePair ->
        val profile = profilePair.first
        val routes = profilePair.second

        // Reduce the route to include only turning points and the final destination
        val turningPoints = routes.flatMap { route ->
            val reducedRoute = mutableListOf<Pair<String, GeoPoint>>()
            var previousBearing: Double? = null

            for (i in 1 until route.size) { // Start from index 1 to compare with the previous point
                val (startLat, startLng) = route[i - 1] // Current point
                val (endLat, endLng) = route[i]         // Next point

                val currentBearing = calculateBearing(startLat, startLng, endLat, endLng)
                if (previousBearing != null) {
                    val turn = getTurnInstruction(previousBearing, currentBearing)
                    if (turn != "Continue straight") {
                        // Add the turn instruction to the **current point**
                        reducedRoute.add(turn to GeoPoint(startLat, startLng))
                    }
                }
                previousBearing = currentBearing
            }

            // Add the final point of the route
            route.lastOrNull()?.let { finalPoint ->
                reducedRoute.add("Continue straight" to GeoPoint(finalPoint.first, finalPoint.second))
            }
            reducedRoute
        }
        profile to turningPoints.toMutableList()
    }.toMutableList()
}

fun getCurrentPoints(
    lineString: MutableList<Pair<String, MutableList<MutableList<Pair<Double, Double>>>>>,
    turningPoints: MutableList<Pair<String, MutableList<Pair<String, GeoPoint>>>>
): MutableList<MutableList<GeoPoint>> {
    val currentPoints = mutableListOf<MutableList<GeoPoint>>()

    // Access the 0th index for both lineString and turningPoints
    val lineSegments = lineString.firstOrNull()?.second?.flatten() ?: return currentPoints
    val turningPointCoordinates = turningPoints.firstOrNull()?.second?.map { it.second } ?: return currentPoints

    // Add points from lineString to turning points
    var segment = mutableListOf<GeoPoint>()
    for (point in lineSegments) {
        val geoPoint = GeoPoint(point.first, point.second)
        segment.add(geoPoint)

        // When encountering a turning point, add the segment to currentPoints
        if (turningPointCoordinates.contains(geoPoint)) {
            currentPoints.add(segment)
            segment = mutableListOf()
        }
    }

    // Ensure the last turning point creates a new segment
    if (segment.isNotEmpty()) {
        currentPoints.add(segment)
    }

    // Handle the last instruction in turningPoints if missed
    val lastTurningPoint = turningPointCoordinates.lastOrNull()
    if (lastTurningPoint != null && !currentPoints.last().contains(lastTurningPoint)) {
        currentPoints.add(mutableListOf(lastTurningPoint))
    }

    return currentPoints
}

fun calculateDistances(currentPoints: MutableList<MutableList<GeoPoint>>): MutableList<Int> {
    val currentDistances = mutableListOf<Int>()
    for (i in currentPoints.indices) {
        // If it's not the first index, prepend the last GeoPoint of the previous segment
        if (i > 0) {
            val lastGeoPointOfPreviousSegment = currentPoints[i - 1].last()
            currentPoints[i].add(0, lastGeoPointOfPreviousSegment) // Add it to the start
        }

        val segmentDistance = calculateCurrentDistance(currentPoints[i])

        currentDistances.add(segmentDistance)
    }

    return currentDistances
}

fun calculateCurrentDistance(segment: MutableList<GeoPoint>): Int {
    var segmentDistance = 0.0

    for (j in 0 until segment.size - 1) {
        val start = segment[j]
        val end = segment[j + 1]
        segmentDistance += haversineInMeters(start.latitude, start.longitude, end.latitude, end.longitude)
    }

    return segmentDistance.toInt()
}


fun getNewRoute(
    context: Context,
    currentProfile: String,
    userLocation: GeoPoint,
    destinations: MutableList<GeoPoint>,
): MutableList<Pair<Double, Double>> {
    val repository = AppDataContainer(context).localRoutingRepository

    // Ensure graphs are initialized only once
    if (!repository.isInitialized) {
        repository.initializeGraphs(context)
    }

    // Safeguard for empty destinations
    if (destinations.isEmpty()) {
        return mutableListOf() // Return an empty list if no destinations are provided
    }

    // Process only the first destination
    val destination = destinations.first()
    val path = repository.getRoute(
        context = context,
        start = "${userLocation.longitude},${userLocation.latitude}",
        end = "${destination.longitude},${destination.latitude}",
        profile = currentProfile
    )

    // Safeguard for empty or invalid route
    if (path.isNotEmpty()) {
        try {
            // Parse route
            val routePoints = parseGeoJSONGeometry(path.first().lineString).toMutableList()

            // Return the route points
            return routePoints
        } catch (e: Exception) {
            Log.e("Route", "Error parsing GeoJSON for destination: ${e.message}")
        }
    }

    // Return an empty list if no valid route is found
    return mutableListOf()
}
suspend fun getNewTransitRoute(
    context: Context,
    currentProfile: String,
    userLocation: GeoPoint,
    route: String,
    viewModel: MapViewModel,
    destinations: MutableList<GeoPoint>
): MutableList<Pair<Double, Double>> {
    val repository = AppDataContainer(context).localRoutingRepository

    // Ensure graphs are initialized only once
    if (!repository.isInitialized) {
        repository.initializeGraphs(context)
    }

    // Safeguard for empty destinations
    if (destinations.isEmpty()) {
        return mutableListOf() // Return an empty list if no destinations are provided
    }

    // Process the first destination
    val destination = destinations.first()

    // Define nearest bus stops and optimal route container
    val nearestStartBusStops: List<BusStop>
    val nearestEndBusStops: List<BusStop>
    var optimalTransitCoordinates: MutableList<Pair<Double, Double>> = mutableListOf()

    when (route) {
        "Forestry" -> {
            // Use Forestry bus stops
            nearestStartBusStops = findNearestBusStops(viewModel.busStopsForestry, userLocation.latitude, userLocation.longitude, 3)
            nearestEndBusStops = findNearestBusStops(viewModel.busStopsForestry, destination.latitude, destination.longitude, 3)

            // Get the most optimal transit route for Forestry
            val forestryRoutes = calculateRoutesForType(
                context = context,
                startBusStops = nearestStartBusStops,
                endBusStops = nearestEndBusStops,
                busRoutes = viewModel.busRoutes.filter { it.name.startsWith("Forestry") },
                repository = repository,
                startLat = userLocation.latitude,
                startLon = userLocation.longitude,
                endLat = destination.latitude,
                endLon = destination.longitude,
                routeTypePrefix = "Forestry"
            )

            // Extract only the "transit" profile and parse the GeoJSON lineString
            optimalTransitCoordinates = forestryRoutes.minByOrNull { it.first }?.third
                ?.find { it.first == "transit" }?.second
                ?.flatMap { routeWithLineString ->
                    parseGeoJSONGeometry(routeWithLineString.lineString)
                }?.toMutableList() ?: mutableListOf()
        }

        "Kaliwa" -> {
            // Use Kaliwa and Short Kaliwa bus stops
            val nearestKaliwaStartBusStops = findNearestBusStops(viewModel.busStopsKaliwa, userLocation.latitude, userLocation.longitude, 3)
            val nearestShortKaliwaStartBusStops = findNearestBusStops(viewModel.shortBusStopsKaliwa, userLocation.latitude, userLocation.longitude, 3)
            val nearestKaliwaEndBusStops = findNearestBusStops(viewModel.busStopsKaliwa, destination.latitude, destination.longitude, 3)
            val nearestShortKaliwaEndBusStops = findNearestBusStops(viewModel.shortBusStopsKaliwa, destination.latitude, destination.longitude, 3)

            // Calculate routes for both Kaliwa and Short Kaliwa
            val kaliwaRoutes = calculateRoutesForType(
                context = context,
                startBusStops = nearestKaliwaStartBusStops,
                endBusStops = nearestKaliwaEndBusStops,
                busRoutes = viewModel.busRoutes.filter { it.name.startsWith("Kaliwa") },
                repository = repository,
                startLat = userLocation.latitude,
                startLon = userLocation.longitude,
                endLat = destination.latitude,
                endLon = destination.longitude,
                routeTypePrefix = "Kaliwa"
            )
            val shortKaliwaRoutes = calculateRoutesForType(
                context = context,
                startBusStops = nearestShortKaliwaStartBusStops,
                endBusStops = nearestShortKaliwaEndBusStops,
                busRoutes = viewModel.busRoutes.filter { it.name.startsWith("Kaliwa") },
                repository = repository,
                startLat = userLocation.latitude,
                startLon = userLocation.longitude,
                endLat = destination.latitude,
                endLon = destination.longitude,
                routeTypePrefix = "Kaliwa"
            )

            // Combine and find the most optimal transit route
            val combinedRoutes = kaliwaRoutes + shortKaliwaRoutes
            optimalTransitCoordinates = combinedRoutes.minByOrNull { it.first }?.third
                ?.find { it.first == "transit" }?.second
                ?.flatMap { routeWithLineString ->
                    parseGeoJSONGeometry(routeWithLineString.lineString)
                }?.toMutableList() ?: mutableListOf()
        }

        "Kanan" -> {
            // Use Kanan and Short Kanan bus stops
            val nearestKananStartBusStops = findNearestBusStops(viewModel.busStopsKanan, userLocation.latitude, userLocation.longitude, 3)
            val nearestShortKananStartBusStops = findNearestBusStops(viewModel.shortBusStopsKanan, userLocation.latitude, userLocation.longitude, 3)
            val nearestKananEndBusStops = findNearestBusStops(viewModel.busStopsKanan, destination.latitude, destination.longitude, 3)
            val nearestShortKananEndBusStops = findNearestBusStops(viewModel.shortBusStopsKanan, destination.latitude, destination.longitude, 3)

            // Calculate routes for both Kanan and Short Kanan
            val kananRoutes = calculateRoutesForType(
                context = context,
                startBusStops = nearestKananStartBusStops,
                endBusStops = nearestKananEndBusStops,
                busRoutes = viewModel.busRoutes.filter { it.name.startsWith("Kanan") },
                repository = repository,
                startLat = userLocation.latitude,
                startLon = userLocation.longitude,
                endLat = destination.latitude,
                endLon = destination.longitude,
                routeTypePrefix = "Kanan"
            )
            val shortKananRoutes = calculateRoutesForType(
                context = context,
                startBusStops = nearestShortKananStartBusStops,
                endBusStops = nearestShortKananEndBusStops,
                busRoutes = viewModel.busRoutes.filter { it.name.startsWith("Kanan") },
                repository = repository,
                startLat = userLocation.latitude,
                startLon = userLocation.longitude,
                endLat = destination.latitude,
                endLon = destination.longitude,
                routeTypePrefix = "Kanan"
            )

            // Combine and find the most optimal transit route
            val combinedRoutes = kananRoutes + shortKananRoutes
            optimalTransitCoordinates = combinedRoutes.minByOrNull { it.first }?.third
                ?.find { it.first == "transit" }?.second
                ?.flatMap { routeWithLineString ->
                    parseGeoJSONGeometry(routeWithLineString.lineString)
                }?.toMutableList() ?: mutableListOf()
        }
    }

    return optimalTransitCoordinates
}
