package com.edison.lakbayuplb.ui.map.routing_algorithm

import com.edison.lakbayuplb.ui.settings.global.RouteSettingsViewModel

data class RouteWithLineString(
    val route: Route,        // The updated route object with legs, distance, etc.
    val colorCode: String,   // Color code for visualizing the route on the map
    val lineString: String,  // GeoJSON LineString representing the full path geometry
    val profile: String      // Profile (e.g., "foot", "bicycle", "car")
)

data class Route(
    val legs: List<Leg>, // Each route can have multiple legs (e.g., in multi-modal routes)
    val distance: Double, // Total distance of the route
    val duration: Double, // Total duration of the route
    val weight: Double // Optional: weight based on factors like traffic, penalties, etc.
)

data class Leg(
    val steps: List<Step>, // Each leg has multiple steps (e.g., "turn left", "continue straight")
    val distance: Double, // Distance of this leg
    val duration: Double, // Duration of this leg
    val weight: Double // Weight for this leg
)

data class Step(
    val geometry: String, // Encoded polyline or GeoJSON geometry for this step
    val distance: Double, // Distance for this step
    val duration: Double, // Duration for this step
    val name: String = "" // Instruction for the step, e.g., "Turn right onto Main St"
)

data class NearestResponse(
    val waypoints: List<Waypoint> // Waypoints in response to nearest points query
)

data class Waypoint(
    val location: List<Double> // Coordinates of the waypoint [longitude, latitude]
)


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
    nodes: List<Node>,
    profile: String,
    routeSettingsViewModel: RouteSettingsViewModel
): Double {
    val totalDistanceMeters = calculateTotalDistance(nodes)

    // Get the speed based on the profile
    val speedMetersPerSecond = when (profile) {
        "foot" -> routeSettingsViewModel.walkingSpeed.value
        "bicycle" -> routeSettingsViewModel.cyclingSpeed.value
        "driving" -> routeSettingsViewModel.carSpeed.value
        "transit" -> routeSettingsViewModel.jeepneySpeed.value
        else -> 1.4 // Default walking speed
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
    path: List<Node>,
    profile: String,
    colorCode: String,
    routeSettingsViewModel: RouteSettingsViewModel
): RouteWithLineString {
    val totalDistance = calculateTotalDistance(path)
    val totalDuration = calculateTotalDuration(path,profile,routeSettingsViewModel)

    // Create a single leg with steps
    val leg = createLeg(path,profile,routeSettingsViewModel)

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
        colorCode = colorCode,
        lineString = geoJsonLineString,
        profile = profile
    )
}

fun createLeg(
    path: List<Node>,
    profile: String,
    routeSettingsViewModel: RouteSettingsViewModel
): Leg {
    val steps = mutableListOf<Step>()

    for (i in 0 until path.size - 1) {
        val startNode = path[i]
        val endNode = path[i + 1]

        // Create a step between each pair of nodes
        val step = Step(
            geometry = nodesToGeoJsonLineString(listOf(startNode, endNode)), // Create a GeoJSON LineString for this step
            distance = haversine(startNode.latitude, startNode.longitude, endNode.latitude, endNode.longitude) * 1000, // Convert to meters
            duration = calculateTotalDuration(listOf(startNode, endNode),profile,routeSettingsViewModel), // Estimate duration based on distance
            name = "Move from Node ${startNode.id} to Node ${endNode.id}" // Placeholder for instructions
        )
        steps.add(step)
    }

    // Return the leg with all the steps
    return Leg(
        steps = steps,
        distance = calculateTotalDistance(path),
        duration = calculateTotalDuration(path,profile,routeSettingsViewModel),
        weight = calculateTotalDistance(path) // Customize weight logic if needed
    )
}

