package com.edison.lakbayuplb.algorithm.routing_algorithm

data class Node(
    val id: Int,
    val latitude: Double,
    val longitude: Double
)

data class Edge(
    val source: Node,
    val destination: Node,
    val weight: Double,
    val isCrossing: Boolean = false // Flag to indicate if this is a crossing
)

open class Graph {
    var nodes: List<Node> = emptyList() // Default to an empty list
    var edges: List<Edge> = emptyList() // Default to an empty list
    var crossings: List<Node> = emptyList()
    // Get neighbors of a node (for Dijkstra's traversal)
    fun getNeighbors(node: Node): List<Node> {
        return edges.filter { it.source == node }.map { it.destination }
    }

    // Get the edge between two nodes
    fun getEdge(source: Node, destination: Node): Edge? {
        return edges.find { it.source == source && it.destination == destination }
    }
}


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
