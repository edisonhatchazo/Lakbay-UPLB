package com.example.classschedule.algorithm.osrms

data class RouteResponse(
    val routes: List<Route>
)

data class Route(
    val legs: List<Leg>,
    val distance: Double,
    val duration: Double,
    val weight: Double
)

data class Leg(
    val steps: List<Step>,
    val distance: Double,
    val duration: Double,
    val weight: Double
)

data class Step(
    val geometry: String,
    val distance: Double,
    val duration: Double
)

data class NearestResponse(val waypoints: List<Waypoint>)
data class Waypoint(val location: List<Double>)