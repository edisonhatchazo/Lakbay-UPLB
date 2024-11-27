package com.edison.lakbayuplb.algorithm.routing_algorithm

import android.util.Log
import com.edison.lakbayuplb.R
import org.json.JSONObject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
fun createGeoJSONLineString(coordinates: List<String>): String {
    return """
        {
            "type": "LineString",
            "coordinates": [${coordinates.joinToString(",")}]
        }
    """.trimIndent()
}


fun parseGeoJSONGeometry(geoJSONString: String): List<Pair<Double, Double>> {

    if (geoJSONString.isBlank()) {
        return emptyList()
    }

    return try {
        val geoJSON = JSONObject(geoJSONString)
        val coordinates = geoJSON.getJSONArray("coordinates")
        val decodedCoordinates = mutableListOf<Pair<Double, Double>>()

        for (i in 0 until coordinates.length()) {
            val point = coordinates.getJSONArray(i)
            val lng = point.getDouble(0)
            val lat = point.getDouble(1)
            decodedCoordinates.add(Pair(lat, lng))
        }

        decodedCoordinates
    } catch (e: Exception) {
        // Log the error for debugging
        Log.e("parseGeoJSONGeometry", "Error parsing GeoJSON: ${e.message}")
        emptyList() // Return an empty list if parsing fails
    }

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
        "Turn left" -> R.drawable.left
        "Turn right" -> R.drawable.right
        "Continue straight" -> R.drawable.straight
        "Move" -> R.drawable.straight
        else -> R.drawable.u_turn_left
    }
}

fun parseGeoJSONCoordinates(lineString: String): List<String> {
    val geoJSON = JSONObject(lineString)
    val coordinatesArray = geoJSON.getJSONArray("coordinates")
    val coordinates = mutableListOf<String>()

    for (i in 0 until coordinatesArray.length()) {
        val coordinatePair = coordinatesArray.getJSONArray(i)
        coordinates.add("[${coordinatePair.getDouble(0)}, ${coordinatePair.getDouble(1)}]")
    }
    return coordinates
}
