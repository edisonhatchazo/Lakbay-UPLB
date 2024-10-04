package com.edison.lakbayuplb.ui.map.routing_algorithm.transit

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.osmdroid.util.GeoPoint
import java.io.InputStreamReader

data class BusRoute(val id: String, val name: String, val coordinates: List<GeoPoint>)


fun loadBusRoutes(context: Context, fileName: String): List<BusRoute> {
    val assetManager = context.assets
    val inputStream = assetManager.open(fileName)
    val json = InputStreamReader(inputStream).use { it.readText() }

    val geoJson = Gson().fromJson(json, JsonObject::class.java)
    val features = geoJson.getAsJsonArray("features")

    return features.mapNotNull {
        val properties = it.asJsonObject.getAsJsonObject("properties")
        val geometry = it.asJsonObject.getAsJsonObject("geometry")
        val type = geometry.get("type").asString

        if (type == "LineString") {
            val coordinatesArray = geometry.getAsJsonArray("coordinates")
            val coordinates = coordinatesArray.mapNotNull { coordElement ->
                val coord = coordElement.asJsonArray
                val lon = coord[0].asDouble
                val lat = coord[1].asDouble
                GeoPoint(lat, lon)
            }


            BusRoute(
                id = properties.get("route").asString,
                name = properties.get("route").asString,
                coordinates = coordinates
            )
        } else {
            null
        }

    }

}

data class RouteCoordinates(val coordinates: String, val routeName: String)


fun findRouteCoordinates(startBusStop: BusStop, endBusStop: BusStop, busRoutes: List<BusRoute>): RouteCoordinates {
    val startLatLng = GeoPoint(startBusStop.lat, startBusStop.lon)
    val endLatLng = GeoPoint(endBusStop.lat, endBusStop.lon)

    val matchingRoute = busRoutes.find { route ->
        route.coordinates.contains(startLatLng) && route.coordinates.contains(endLatLng)
    }

    if (matchingRoute != null) {
        val startIndex = matchingRoute.coordinates.indexOf(startLatLng)
        val endIndex = matchingRoute.coordinates.indexOf(endLatLng)
        val subList = matchingRoute.coordinates.subList(startIndex, endIndex + 1)

        val coordinates = subList.joinToString(";") { "${it.longitude},${it.latitude}" }
        return RouteCoordinates(coordinates, matchingRoute.name)
    } else {
        throw IllegalArgumentException("No valid route found between bus stops")
    }
}
