package com.edison.lakbayuplb.algorithm.routing_algorithm

import android.content.Context
import com.edison.lakbayuplb.algorithm.routing_algorithm.transit.BusRoute
import com.edison.lakbayuplb.algorithm.routing_algorithm.transit.ParkingSpot
import com.edison.lakbayuplb.algorithm.routing_algorithm.transit.findNearestParkingSpot
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
        val profiles = listOf("foot", "bicycle", "driving","transit")
        for (profile in profiles) {
            val graph = parseGeoJSON(context, profile)
            graphCache[profile] = graph
        }
        isInitialized = true
    }

    private fun getGraph(profile: String): Graph? {
        if (!isInitialized) {
            return null
        }
        return graphCache[profile]
    }

    fun getRoute(
        context: Context,
        profile: String,
        start: String,
        end: String,
        colorCode: String,

    ): MutableList<RouteWithLineString> {
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
            val routeWithLineString = createRouteWithLineString(context,path, profile, colorCode) // Replace color code dynamically
            return mutableListOf(routeWithLineString)
        } else {
            return mutableListOf()
        }
    }

    fun getRouteWithParking(
        context: Context,
        profile: String,
        start: String,
        end: String,
        colorCode: String,
        parkingSpots: List<ParkingSpot>,
        radius: Double,
    ): Pair<MutableList<RouteWithLineString>, MutableList<RouteWithLineString>> {
        val destination = GeoPoint(end.split(",")[1].toDouble(), end.split(",")[0].toDouble())
        val nearestParkingSpot = findNearestParkingSpot(destination, parkingSpots.map { GeoPoint(it.latitude, it.longitude) }, radius)

        return nearestParkingSpot?.let { parkingSpot ->
            val parkingCoordinates = "${parkingSpot.longitude},${parkingSpot.latitude}"

            // Main route (car or bicycle) to the parking spot, converted to mutable
            val carOrCyclingRoute = getRoute(context,profile, start, parkingCoordinates, colorCode).toMutableList()

            // Walking route from parking spot to destination, converted to mutable
            val walkingRoute = getRoute(context,"foot", parkingCoordinates, end, "#00FF00").toMutableList()

            // Return both routes as mutable lists
            carOrCyclingRoute to walkingRoute
        } ?: run {
            // Fallback to a direct route if no parking spot is found, with empty mutable walking route
            val directRoute = getRoute(context,profile, start, end, colorCode).toMutableList()
            directRoute to mutableListOf()
        }

    }


    fun getRouteWithPredefinedPath(
        context: Context,
        profile: String,
        predefinedCoordinates: String,
        colorCode: String
    ): MutableList<RouteWithLineString> {
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
                context,
                profile,
                start,
                end,
                colorCode

            )

            // Add this segment to the overall route
            routeSegments.addAll(segmentRoute)
        }

        return routeSegments
    }
}