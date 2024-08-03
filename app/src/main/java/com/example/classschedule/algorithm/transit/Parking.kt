package com.example.classschedule.algorithm.transit
import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.maplibre.android.geometry.LatLng
import java.io.InputStreamReader
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class ParkingSpot(val name: String, val latitude: Double, val longitude: Double)


fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371000 // Radius of the Earth in meters
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return r * c // Distance in meters
}

fun findNearestParkingSpot(destination: LatLng, parkingSpots: List<LatLng>, radius: Double): LatLng? {
    return parkingSpots
        .filter { calculateDistance(destination.latitude, destination.longitude, it.latitude, it.longitude) <= radius }
        .minByOrNull { calculateDistance(destination.latitude, destination.longitude, it.latitude, it.longitude) }
}

fun loadParkingSpots(context: Context, fileName: String): List<ParkingSpot> {
    val assetManager = context.assets
    val inputStream = assetManager.open(fileName)
    val json = InputStreamReader(inputStream).use { it.readText() }

    val geoJson = Gson().fromJson(json, JsonObject::class.java)
    val features = geoJson.getAsJsonArray("features")

    return features.mapNotNull {
        val properties = it.asJsonObject.getAsJsonObject("properties")
        val geometry = it.asJsonObject.getAsJsonObject("geometry")
        val type = geometry.get("type").asString

        if (type == "Point") {
            val coordinates = geometry.getAsJsonArray("coordinates")
            val lon = coordinates[0].asDouble
            val lat = coordinates[1].asDouble
            val name = properties.get("name")?.asString ?: "Unnamed Parking Spot"

            ParkingSpot(name, lat, lon)
        } else {
            null
        }
    }
}

fun loadAllParkingSpots(context: Context): List<ParkingSpot> {
    return loadParkingSpots(context, "UPLB_Parking.geojson")
}
