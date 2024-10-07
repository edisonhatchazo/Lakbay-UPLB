package com.edison.lakbayuplb.algorithm.routing_algorithm

import java.util.PriorityQueue

fun dijkstra(graph: Graph, start: Node, goal: Node): List<Node> {
    val distanceMap = mutableMapOf<Node, Double>().withDefault { Double.MAX_VALUE }
    val previousNode = mutableMapOf<Node, Node?>()
    val unvisited = PriorityQueue<Node>(compareBy { distanceMap.getValue(it) })

    distanceMap[start] = 0.0
    unvisited.add(start)

    while (unvisited.isNotEmpty()) {
        val current = unvisited.poll()

        // If we've reached the goal node, we can reconstruct the path
        if (current == goal) {
            return reconstructPath(previousNode, current)
        }

        for (neighbor in current?.let { graph.getNeighbors(it) }!!) {
            val edge = graph.getEdge(current, neighbor) ?: continue
            val newDistance = distanceMap.getValue(current) + edge.weight

            if (newDistance < distanceMap.getValue(neighbor)) {
                distanceMap[neighbor] = newDistance
                previousNode[neighbor] = current
                unvisited.add(neighbor)
            }
        }
    }

    return emptyList() // No path found
}

fun reconstructPath(cameFrom: Map<Node, Node?>, current: Node): List<Node> {
    var curr: Node? = current
    val path = mutableListOf<Node>()

    while (curr != null) {
        path.add(curr)
        curr = cameFrom[curr]
    }

    return path.reversed()
}
