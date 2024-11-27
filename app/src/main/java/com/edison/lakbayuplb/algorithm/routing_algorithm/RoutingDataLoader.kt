package com.edison.lakbayuplb.algorithm.routing_algorithm

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.osmdroid.util.GeoPoint
import java.io.InputStream

suspend fun parseBounds(context: Context): Map<String, List<GeoPoint>> = withContext(Dispatchers.IO) {
    val boundsMap = mutableMapOf<String, List<GeoPoint>>()
    val assetManager = context.assets
    val inputStream = assetManager.open("UPLB_Bounds.geojson")
    val geoJsonString = inputStream.bufferedReader().use { it.readText() }

    val jsonObject = JSONObject(geoJsonString)
    val features = jsonObject.getJSONArray("features")

    for (i in 0 until features.length()) {
        val feature = features.getJSONObject(i)
        val properties = feature.getJSONObject("properties")
        val geometry = feature.getJSONObject("geometry")

        if (geometry.getString("type") == "Polygon") {
            val coordinatesArray = geometry.getJSONArray("coordinates").getJSONArray(0)
            val coordinates = (0 until coordinatesArray.length()).map { index ->
                val point = coordinatesArray.getJSONArray(index)
                GeoPoint(point.getDouble(1), point.getDouble(0))
            }

            val name = properties.optString("name", "")
            if (name == "UPLB" || name == "CAS") {
                boundsMap[name] = coordinates
            }
        }
    }
    return@withContext boundsMap
}





fun parseGeoJSON(context: Context, profile: String): Graph = runBlocking {
    val graph = Graph()

    // Load the GeoJSON file from assets
    val assetManager = context.assets
    val inputStream: InputStream = assetManager.open("map/osm_profiles/$profile.geojson")
    val geoJsonString = inputStream.bufferedReader().use { it.readText() }

    val jsonObject = JSONObject(geoJsonString)
    val features = jsonObject.getJSONArray("features")

    val nodeList = mutableMapOf<String, Node>()  // Map to store nodes and avoid duplicates
    val edgeList = mutableListOf<Edge>()

    for (i in 0 until features.length()) {
        val feature = features.getJSONObject(i)
        val properties = feature.getJSONObject("properties")
        val geometry = feature.getJSONObject("geometry")
        val coordinates = geometry.getJSONArray("coordinates")

        // Detect if the road is one-way
        val isOneWay = properties.optString("oneway") == "yes"

        if (geometry.getString("type") == "LineString") {
            // Iterate through the coordinates to build edges
            for (j in 0 until coordinates.length() - 1) {
                val startLat = coordinates.getJSONArray(j).getDouble(1)
                val startLng = coordinates.getJSONArray(j).getDouble(0)
                val nextLat = coordinates.getJSONArray(j + 1).getDouble(1)
                val nextLng = coordinates.getJSONArray(j + 1).getDouble(0)

                val startNodeId = "$startLat,$startLng"
                val nextNodeId = "$nextLat,$nextLng"

                val startNode = nodeList.getOrPut(startNodeId) {
                    Node(id = startNodeId.hashCode(), latitude = startLat, longitude = startLng)
                }

                val endNode = nodeList.getOrPut(nextNodeId) {
                    Node(id = nextNodeId.hashCode(), latitude = nextLat, longitude = nextLng)
                }

                val distance = haversine(startNode.latitude, startNode.longitude, endNode.latitude, endNode.longitude)

                // Create forward edge
                edgeList.add(Edge(source = startNode, destination = endNode, weight = distance))

                // Only add the reverse edge if it's not a one-way road
                if (!isOneWay) {
                    edgeList.add(Edge(source = endNode, destination = startNode, weight = distance))
                }
            }
        }
    }

    graph.nodes = nodeList.values.toList()
    graph.edges = edgeList
    return@runBlocking graph
}

