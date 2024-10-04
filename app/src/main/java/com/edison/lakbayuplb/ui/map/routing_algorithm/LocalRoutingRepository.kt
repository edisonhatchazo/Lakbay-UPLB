package com.edison.lakbayuplb.ui.map.routing_algorithm

import android.content.Context
import android.util.Log
import com.edison.lakbayuplb.ui.map.routing_algorithm.transit.BusRoute
import com.edison.lakbayuplb.ui.map.routing_algorithm.transit.ParkingSpot
import com.edison.lakbayuplb.ui.map.routing_algorithm.transit.findNearestParkingSpot
import com.edison.lakbayuplb.ui.settings.global.RouteSettingsViewModel
import org.osmdroid.util.GeoPoint


class LocalRoutingRepository(
    private val busRoutes: List<BusRoute>
) {
    private val graphCache = mutableMapOf<String, Graph>()
    private var isInitialized = false

    fun getBusRoutes(): List<BusRoute>{
        return busRoutes
    }

    // This method is called to parse GeoJSON during app initialization
    fun initializeGraphs(context: Context) {
        val profiles = listOf("foot", "bicycle", "driving")
        for (profile in profiles) {
            val graph = parseGeoJSON(context, profile)
            graphCache[profile] = graph
        }
        isInitialized = true
    }

    fun getGraph(profile: String): Graph? {
        if (!isInitialized) {
            Log.e("Map", "Graphs are not initialized yet!")
            return null
        }
        return graphCache[profile]
    }

    fun getRoute(
        profile: String,
        start: String,
        end: String,
        colorCode: String,
        routeSettingsViewModel: RouteSettingsViewModel
    ): List<RouteWithLineString> {
        val startLatLng = start.split(",")
        val endLatLng = end.split(",")

        val startNode = Node(id = 0, latitude = startLatLng[1].toDouble(), longitude = startLatLng[0].toDouble())
        val endNode = Node(id = 1, latitude = endLatLng[1].toDouble(), longitude = endLatLng[0].toDouble())

        // Parse the corresponding GeoJSON data based on the profile (bicycle, driving, foot)
        val graph = getGraph(profile) ?: throw IllegalStateException("Graph not initialized for profile: $profile")

        // Find nearest nodes in the graph
        val nearestStart = findNearestNode(startNode, graph.nodes)
        val nearestEnd = findNearestNode(endNode, graph.nodes)
        // Calculate the route using Dijkstra's algorithm
        val path = aStar(graph, nearestStart, nearestEnd)
        if (path.isNotEmpty()) {
            // Create the route with line string and return
            val routeWithLineString = createRouteWithLineString(path, profile, colorCode,routeSettingsViewModel) // Replace color code dynamically
            return listOf(routeWithLineString)
        } else {
            return emptyList()
        }
    }

    fun getRouteWithParking(
        profile: String,
        start: String,
        end: String,
        colorCode: String,
        parkingSpots: List<ParkingSpot>,
        radius: Double,
        routeSettingsViewModel: RouteSettingsViewModel
    ): List<RouteWithLineString> {
        val destination = GeoPoint(end.split(",")[1].toDouble(), end.split(",")[0].toDouble())
        val nearestParkingSpot = findNearestParkingSpot(destination, parkingSpots.map { GeoPoint(it.latitude, it.longitude) }, radius)

        return nearestParkingSpot?.let { parkingSpot ->
            val parkingCoordinates = "${parkingSpot.longitude},${parkingSpot.latitude}"

            // Driving or Cycling route to parking spot
            val carOrCyclingRoute = getRoute(profile, start, parkingCoordinates, colorCode,routeSettingsViewModel)

            // Walking route from parking spot to destination
            val walkingRoute = getRoute("foot", parkingCoordinates, end, "#00FF00",routeSettingsViewModel)

            // Combine both routes (drive to parking + walk to destination)
            carOrCyclingRoute + walkingRoute
        } ?: run {
            // Fallback to normal route if no parking spot is found
            getRoute(profile, start, end, colorCode,routeSettingsViewModel)
        }
    }

    fun getRouteWithPredefinedPath(
        profile: String,
        predefinedCoordinates: String,
        colorCode: String,
        routeSettingsViewModel: RouteSettingsViewModel
    ): List<RouteWithLineString> {
        val coordinates = predefinedCoordinates.split(";")
        val routeSegments = mutableListOf<RouteWithLineString>()

        // Iterate through each consecutive pair of bus stops
        for (i in 0 until coordinates.size - 1) {
            val startLatLng = coordinates[i].split(",")
            val endLatLng = coordinates[i + 1].split(",")

            val start = "${startLatLng[0]},${startLatLng[1]}"
            val end = "${endLatLng[0]},${endLatLng[1]}"

            // Use the `getRoute` function to calculate the route between these two points
            val segmentRoute = getRoute(
                profile,
                start,
                end,
                colorCode,
                routeSettingsViewModel
            )

            // Add this segment to the overall route
            routeSegments.addAll(segmentRoute)
        }

        return routeSegments
    }



}