package com.edison.lakbayuplb.ui.map

import android.content.Context
import android.util.Log
import com.edison.lakbayuplb.algorithm.routing_algorithm.calculateBearing
import com.edison.lakbayuplb.algorithm.routing_algorithm.getTurnInstruction
import com.edison.lakbayuplb.algorithm.routing_algorithm.haversineInMeters
import com.edison.lakbayuplb.algorithm.routing_algorithm.parseGeoJSONGeometry
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
            points.lastOrNull()?.let { GeoPoint(it.first, it.second) }
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

fun generateInstructionsFromLineString(lineString: MutableList<Pair<Double, Double>>): MutableList<String> {
    val instructions = mutableListOf<String>()
    var previousBearing: Double? = null
    var totalStraightDistance = 0.0 // Accumulate straight distances

    for (i in 0 until lineString.size - 1) {
        val (startLat, startLng) = lineString[i]
        val (endLat, endLng) = lineString[i + 1]
        val currentBearing = calculateBearing(startLat, startLng, endLat, endLng)

        // Calculate the distance between the two points
        val distance = haversineInMeters(startLat, startLng, endLat, endLng)
        val instruction: String = if (previousBearing != null) {
            val turnInstruction = getTurnInstruction(previousBearing, currentBearing)

            if (turnInstruction == "Continue straight") {
                // Accumulate straight distances
                totalStraightDistance += distance
                continue // Skip adding an instruction now
            } else {
                // Add accumulated "Continue straight" distance before the turn
                val turnWithDistance = if (totalStraightDistance > 0) {
                    "$turnInstruction in ${(totalStraightDistance + distance).toInt()} meters."

                } else {
                    "$turnInstruction in ${distance.toInt()} meters."
                }
                totalStraightDistance = 0.0 // Reset straight distance accumulator
                turnWithDistance
            }
        } else {
            // Initial step or no previous bearing
            if (distance > 50) {
                totalStraightDistance += distance
                continue // Skip adding this instruction for now
            } else {
                "Move ${distance.toInt()} meters to the next point."
            }
        }

        instructions.add(instruction) // Add the generated instruction to the list
        previousBearing = currentBearing // Update the previous bearing for the next step
    }

    // Add any remaining "Continue straight" distance as the last instruction
    if (totalStraightDistance > 0) {
        instructions.add("Continue straight for ${totalStraightDistance.toInt()} meters.")
    }

    return instructions
}


fun calculateTotalDistance(lineString: MutableList<Pair<Double, Double>>, currentLocation: GeoPoint): Double {
    var totalDistance = 0.0

    if (lineString.isNotEmpty()) {
        // Distance from the current location to the first point in the lineString
        val firstPoint = lineString.first()
        totalDistance += haversineInMeters(
            currentLocation.latitude, currentLocation.longitude,
            firstPoint.first, firstPoint.second
        )

        // Distance between subsequent points in the lineString
        for (i in 0 until lineString.size - 1) {
            val start = lineString[i]
            val end = lineString[i + 1]
            totalDistance += haversineInMeters(start.first, start.second, end.first, end.second)
        }
    }

    return totalDistance
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

fun getDeviations(
    context: Context,
    userLocation: GeoPoint,
    destinationLocation: GeoPoint,
    deviation: Double,
    currentDistance: Double,
): Pair<Double,Double>{
    val repository = AppDataContainer(context).localRoutingRepository

    // Ensure graphs are initialized only once
    if (!repository.isInitialized) {
        repository.initializeGraphs(context)
    }
    val route = repository.getRoute(
        context = context,
        profile = "transit",
        start = "${userLocation.longitude},${userLocation.latitude}",
        end = "${destinationLocation.longitude},${destinationLocation.latitude}"
    )
    if(route.isEmpty()){
        return Pair(currentDistance,deviation)
    }

    val totalDistance = route.sumOf { it.route.distance }
    val difference = totalDistance - currentDistance

    val newDeviation = if((deviation + difference) <=-20) 0.0 else deviation + difference

    return Pair(totalDistance,newDeviation)
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

fun getDistance(
    context: Context,
    userLocation: GeoPoint,
    destinationLocation: GeoPoint,
    profile:String
):Double{
    val repository = AppDataContainer(context).localRoutingRepository

    // Ensure graphs are initialized only once
    if (!repository.isInitialized) {
        repository.initializeGraphs(context)
    }
    val route = repository.getRoute(
        context = context,
        profile = profile,
        start = "${userLocation.longitude},${userLocation.latitude}",
        end = "${destinationLocation.longitude},${destinationLocation.latitude}"
    )
    if(route.isEmpty()){
        return 0.0
    }

    return route.sumOf { it.route.distance }
}