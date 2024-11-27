package com.edison.lakbayuplb.algorithm.routing_algorithm

import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6372.8 // Earth radius in kilometers
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2).pow(2.0) + sin(dLon / 2).pow(2.0) * cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2))
    val c = 2 * asin(sqrt(a))
    val distance = r * c
    return distance
}


fun haversineInMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6372.8 // Earth radius in kilometers
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2).pow(2.0) + sin(dLon / 2).pow(2.0) * cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2))
    val c = 2 * asin(sqrt(a))
    val distance = r * c * 1000
    return distance
}