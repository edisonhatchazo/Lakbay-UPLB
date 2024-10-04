package com.edison.lakbayuplb.ui.map.routing_algorithm

import java.util.PriorityQueue

fun aStar(graph: Graph, start: Node, goal: Node): List<Node> {
    val distanceMap = mutableMapOf<Node, Double>().withDefault { Double.MAX_VALUE }
    val heuristicMap = mutableMapOf<Node, Double>().withDefault { Double.MAX_VALUE }
    val previousNode = mutableMapOf<Node, Node?>()
    val unvisited = PriorityQueue<Node>(compareBy { distanceMap.getValue(it) + heuristicMap.getValue(it) })

    distanceMap[start] = 0.0
    heuristicMap[start] = haversine(start.latitude, start.longitude, goal.latitude, goal.longitude)
    unvisited.add(start)

    while (unvisited.isNotEmpty()) {
        val current = unvisited.poll()

        if (current == goal) {
            return reconstructPath(previousNode, current)
        }

        val neighbors = current?.let { graph.getNeighbors(it) }

        if (neighbors != null) {
            for (neighbor in neighbors) {
                val edge = graph.getEdge(current, neighbor) ?: continue

                val tentativeDistance = distanceMap.getValue(current) + edge.weight

                if (tentativeDistance < distanceMap.getValue(neighbor)) {
                    distanceMap[neighbor] = tentativeDistance
                    previousNode[neighbor] = current
                    heuristicMap[neighbor] = haversine(neighbor.latitude, neighbor.longitude, goal.latitude, goal.longitude)
                    unvisited.add(neighbor)
                }
            }
        }
    }

    return emptyList() // No path found
}
