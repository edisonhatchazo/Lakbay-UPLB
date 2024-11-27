package com.edison.lakbayuplb.algorithm.routing_algorithm

import android.content.Context
import com.edison.lakbayuplb.ui.settings.global.SpeedPreferences


fun calculateTotalDistance(nodes: List<Node>): Double {
    var totalDistance = 0.0
    for (i in 0 until nodes.size - 1) {
        totalDistance += haversine(
            nodes[i].latitude, nodes[i].longitude,
            nodes[i + 1].latitude, nodes[i + 1].longitude
        )
    }
    return totalDistance * 1000 // Convert from kilometers to meters
}

fun calculateTotalDuration(
    context: Context,
    nodes: List<Node>,
    profile: String,
): Double {
    val totalDistanceMeters = calculateTotalDistance(nodes)
    val speedPreferences = SpeedPreferences(context)
    // Get the speed based on the profile
    val speedMetersPerSecond = when (profile) {
        "foot" -> speedPreferences.walkingSpeed
        "bicycle" -> speedPreferences.cyclingSpeed
        "driving" -> 20.0
        "transit" -> 10.0
        else -> 1.0 // Default walking speed
    }

    // Calculate duration based on speed
    return totalDistanceMeters / speedMetersPerSecond // Duration in seconds
}

fun nodesToGeoJsonLineString(nodes: List<Node>): String {
    // Create a GeoJSON LineString from the list of nodes
    val coordinates = nodes.joinToString(", ") { node ->
        "[${node.longitude}, ${node.latitude}]" // Format as [longitude, latitude]
    }

    // Return the complete GeoJSON LineString object
    return """{
        "type": "LineString",
        "coordinates": [$coordinates]
    }"""
}


// Helper function to find the nearest node
fun findNearestNode(targetNode: Node, nodes: List<Node>): Node {
    return nodes.minByOrNull { haversine(targetNode.latitude, targetNode.longitude, it.latitude, it.longitude) }!!
}

fun createRouteWithLineString(
    context: Context,
    path: List<Node>,
    profile: String
): RouteWithLineString {
    val totalDistance = calculateTotalDistance(path)
    val totalDuration = calculateTotalDuration(context,path,profile)

    // Create a single leg with steps
    val leg = createLeg(context,path,profile)

    // Create the route with the single leg
    val route = Route(
        legs = listOf(leg),
        distance = totalDistance,
        duration = totalDuration,
        weight = totalDistance // Customize the weight logic if needed
    )

    // Create the GeoJSON LineString for the full path
    val geoJsonLineString = nodesToGeoJsonLineString(path)

    // Return RouteWithLineString, including colorCode and profile
    return RouteWithLineString(
        route = route,
        lineString = geoJsonLineString,
        profile = profile
    )
}

fun createLeg(
    context: Context,
    path: List<Node>,
    profile: String,
): Leg {
    val steps = mutableListOf<Step>()

    for (i in 0 until path.size - 1) {
        val startNode = path[i]
        val endNode = path[i + 1]

        // Create a step between each pair of nodes
        val step = Step(
            geometry = nodesToGeoJsonLineString(listOf(startNode, endNode)), // Create a GeoJSON LineString for this step
            distance = haversine(startNode.latitude, startNode.longitude, endNode.latitude, endNode.longitude) * 1000, // Convert to meters
            duration = calculateTotalDuration(context,listOf(startNode, endNode),profile), // Estimate duration based on distance
        )
        steps.add(step)
    }

    // Return the leg with all the steps
    return Leg(
        steps = steps,
        distance = calculateTotalDistance(path),
        duration = calculateTotalDuration(context,path,profile),
        weight = calculateTotalDistance(path) // Customize weight logic if needed
    )
}

