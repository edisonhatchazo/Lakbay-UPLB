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
