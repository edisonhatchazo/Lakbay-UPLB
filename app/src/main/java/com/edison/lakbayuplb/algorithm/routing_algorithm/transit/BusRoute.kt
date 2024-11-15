package com.edison.lakbayuplb.algorithm.routing_algorithm.transit

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

/*
* [versions]
accompanistPermissions = "0.26.5-rc"
activityKtx = "1.9.2"
agp = "8.4.2"
androidPluginAnnotationV9 = "3.0.0"
androidSdkVersion = "11.0.1"
constraintlayoutCompose = "1.0.1"
converterGson = "2.9.0"
fragmentKtx = "1.3.6"
kotlin = "1.9.0"
coreKtx = "1.13.1"
junit = "4.13.2"
junitVersion = "1.1.5"
espressoCore = "3.5.1"
lifecycleRuntimeKtx = "2.8.1"
activityCompose = "1.9.0"
composeBom = "2023.08.00"
nanohttpd = "2.3.1"
osmdroidAndroid = "6.1.20"
retrofit = "2.9.0"
roomKtx = "2.6.1"
roomRuntime = "2.6.1"
navigationRuntimeKtx = "2.7.7"
navigationCompose = "2.7.7"
places = "3.5.0"
runtimeLivedata = "1.6.7"
material = "1.12.0"
ui = "1.6.8"
protoliteWellKnownTypes = "18.0.0"
playServicesPhenotype = "17.0.0"
material3Android = "1.3.0"
* */
