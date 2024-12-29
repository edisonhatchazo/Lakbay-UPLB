package com.edison.lakbayuplb.algorithm.routing_algorithm.transit

import android.content.Context
import com.edison.lakbayuplb.algorithm.routing_algorithm.haversine
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.InputStreamReader

data class BusStop(val name: String, val lat: Double, val lon: Double)
data class BusStops(
    val kaliwaStops: List<BusStop>,
    val kananStops: List<BusStop>,
    val forestryStops: List<BusStop>,
    val shortKaliwaStops: List<BusStop>,
    val shortKananStops: List<BusStop>
)
fun loadBusStops(context: Context, fileName: String): List<BusStop> {
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
            val name = properties.get("name")?.asString ?: "Unnamed Bus Stop"

            BusStop(name, lat, lon)
        } else {
            null
        }
    }
}

fun loadAllBusStops(context: Context): BusStops {
    val kaliwaStops = loadBusStops(context, "Kaliwa.geojson")
    val kananStops = loadBusStops(context, "Kanan.geojson")
    val forestryStops = loadBusStops(context, "Forestry_Stops.geojson")
    val shortKaliwaStops = loadBusStops(context, "Short_Kaliwa.geojson")
    val shortKananStops = loadBusStops(context, "Short_Kanan.geojson")
    return BusStops(kaliwaStops, kananStops, forestryStops, shortKaliwaStops, shortKananStops)
}


fun findNearestBusStops(busStops: List<BusStop>, lat: Double, lon: Double, limit: Int, maxDistance: Double = Double.MAX_VALUE): List<BusStop> {
    return busStops
        .asSequence()
        .map { it to haversine(it.lat, it.lon, lat, lon) }
        .filter { it.second <= maxDistance }
        .sortedBy { it.second }
        .take(limit)
        .map { it.first }
        .toList()
}
