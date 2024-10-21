package com.edison.lakbayuplb.algorithm.routing_algorithm

import com.edison.lakbayuplb.R
import org.json.JSONObject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

fun generateRouteInstructions(routeWithLineString: RouteWithLineString): List<String> {
    val instructions = mutableListOf<String>()
    var totalStraightDistance = 0.0 // To accumulate straight distances

    for (leg in routeWithLineString.route.legs) {
        var previousBearing: Double? = null

        for (step in leg.steps) {
            val distance = step.distance // Distance in meters
            val coordinates = parseGeoJSONGeometry(step.geometry)
            if (coordinates.size < 2) continue // Skip if there aren't enough coordinates

            val (startLat, startLng) = coordinates[0]
            val (endLat, endLng) = coordinates[coordinates.size - 1]
            val currentBearing = calculateBearing(startLat, startLng, endLat, endLng)

            // Determine the instruction based on the change in bearings
            val instruction: String = if (previousBearing != null) {
                val turnInstruction = getTurnInstruction(previousBearing, currentBearing)

                if (turnInstruction == "Continue straight") {
                    // Accumulate straight distances
                    totalStraightDistance += distance
                    continue // Skip adding this instruction for now
                } else {
                    // Add accumulated "Continue straight" distance to the next turn
                    val turnWithDistance = if (totalStraightDistance > 0) {
                        "$turnInstruction in ${(distance + totalStraightDistance).toInt()} meters."
                    } else {
                        "$turnInstruction in ${distance.toInt()} meters."
                    }
                    totalStraightDistance = 0.0 // Reset the accumulated straight distance

                    // Check for crossing and append crossing instruction
                    if (step.isCrossing()) {
                        "$turnWithDistance Carefully cross the street."
                    } else {
                        turnWithDistance
                    }
                }
            } else {
                if (distance > 50) {
                    totalStraightDistance += distance
                    continue // Skip adding this instruction for now
                } else {
                    "Move ${distance.toInt()} meters to the next point."
                }
            }

            // Add the generated instruction to the list
            instructions.add(instruction)
            previousBearing = currentBearing // Update the previous bearing for the next iteration
        }
    }

    // If there's any remaining "Continue straight" distance at the end, add it as the last instruction
    if (totalStraightDistance > 0) {
        instructions.add("Continue straight for ${totalStraightDistance.toInt()} meters.")
    }

    return instructions
}

// Simulated method to check if a step is a crossing
fun Step.isCrossing(): Boolean {
    // This can be a property in the step data, e.g., a flag or a street name that indicates a crossing
    return this.name.contains("Crossing") || this.name.contains("Street")
}

fun parseGeoJSONGeometry(geoJSONString: String): List<Pair<Double, Double>> {
    val geoJSON = JSONObject(geoJSONString)
    val coordinates = geoJSON.getJSONArray("coordinates")
    val decodedCoordinates = mutableListOf<Pair<Double, Double>>()

    for (i in 0 until coordinates.length()) {
        val lng = coordinates.getJSONArray(i).getDouble(0)
        val lat = coordinates.getJSONArray(i).getDouble(1)
        decodedCoordinates.add(Pair(lat, lng))
    }

    return decodedCoordinates
}

// Function to determine the turn based on the change in bearings
fun getTurnInstruction(previousBearing: Double, currentBearing: Double): String {
    val bearingDifference = (currentBearing - previousBearing + 360) % 360

    return when {
        bearingDifference > 45 && bearingDifference <= 135 -> "Turn right"
        bearingDifference >= 225 && bearingDifference < 315 -> "Turn left"
        bearingDifference in 135.0..225.0 -> "Make a U-turn"
        else -> "Continue straight"
    }
}

fun calculateBearing(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val lat1Rad = Math.toRadians(lat1)
    val lat2Rad = Math.toRadians(lat2)
    val deltaLon = Math.toRadians(lon2 - lon1)

    val y = sin(deltaLon) * cos(lat2Rad)
    val x = cos(lat1Rad) * sin(lat2Rad) - sin(lat1Rad) * cos(lat2Rad) * cos(deltaLon)

    return (Math.toDegrees(atan2(y, x)) + 360) % 360 // Normalize the bearing to 0-360
}

fun extractDirectionAndDistance(instruction: String): Pair<String, Int> {

    val directionRegex = Regex("(Turn (right|left)|Make a U-turn|Continue straight|Move)")

    val distanceRegex = Regex("(\\d+) meters")

    // Find the direction from the instruction
    val directionMatch = directionRegex.find(instruction)
    val direction = directionMatch?.value ?: "Unknown direction"

    // Find the distance from the instruction
    val distanceMatch = distanceRegex.find(instruction)
    val distance = distanceMatch?.groupValues?.get(1)?.toIntOrNull() ?: 0

    return Pair(direction, distance)
}

fun getImage(direction: String): Int{
    return when (direction) {
        "left" -> R.drawable.left
        "right" -> R.drawable.right
        "straight" -> R.drawable.straight
        "Move" -> R.drawable.straight
        else -> R.drawable.u_turn_left
    }
}