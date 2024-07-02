package com.example.classschedule.ui.map

import android.Manifest
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.classschedule.R
import com.example.classschedule.algorithm.osrms.RouteResponse
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.plugins.annotation.SymbolManager
import org.maplibre.android.plugins.annotation.SymbolOptions
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.PropertyFactory.lineColor
import org.maplibre.android.style.layers.PropertyFactory.lineWidth
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.LineString
import org.maplibre.geojson.Point
import org.maplibre.geojson.utils.PolylineUtils


enum class OSMCustomMapType( val styleUrl: String) {
    STREET("https://api.maptiler.com/maps/streets-v2/style.json?key=w30aQM8FugoPfybqIZz7"),
    OSM("https://api.maptiler.com/maps/openstreetmap/style.json?key=w30aQM8FugoPfybqIZz7"),
    SATELLITE("https://api.maptiler.com/maps/satellite/style.json?key=w30aQM8FugoPfybqIZz7"),
    LANDSCAPE( "https://api.maptiler.com/maps/landscape/style.json?key=w30aQM8FugoPfybqIZz7")
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun OSMMainMap(
    location: LatLng,
    initialLocation: LatLng?,
    destinationLocation: LatLng?,
    routeResponse: List<Pair<RouteResponse, String>>?,
    styleUrl: String,
) {
    val context = LocalContext.current
    val apiKey = context.getString(R.string.kento)
    val tileServer: WellKnownTileServer = WellKnownTileServer.MapLibre
    val permissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    LaunchedEffect(Unit) {
        permissionsState.launchMultiplePermissionRequest()
    }


    if (permissionsState.allPermissionsGranted) {
        val mapViewState = remember { mutableStateOf<MapView?>(null) }

        MapLibre.getInstance(context, apiKey, tileServer)

        DisposableEffect(Unit) {
            onDispose {
                mapViewState.value?.onStop()
                mapViewState.value?.onDestroy()
                mapViewState.value = null
            }
        }

        LaunchedEffect(key1 = initialLocation, key2 = destinationLocation, key3 = routeResponse) {
            // Invalidate the current map view to trigger reinitialization
            mapViewState.value?.onStop()
            mapViewState.value?.onDestroy()
            mapViewState.value = null
        }

        mapViewState.value?.let { mapView ->


            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {

                    MapView(context).apply {
                        getMapAsync { mapLibreMap ->
                            mapLibreMap.setStyle(styleUrl) { style ->
                                style.addImage("destination-icon", BitmapFactory.decodeResource(context.resources, R.drawable.marker_48))
                                style.addImage("initial-icon", BitmapFactory.decodeResource(context.resources, R.drawable.icons8_circle_18___))
                                style.addImage("walking-icon", BitmapFactory.decodeResource(context.resources, R.drawable.walking_icon))
                                style.addImage("cycling-icon", BitmapFactory.decodeResource(context.resources, R.drawable.cycling_icon))
                                style.addImage("car-icon", BitmapFactory.decodeResource(context.resources, R.drawable.car_icon))
                                style.addImage("transit-icon", BitmapFactory.decodeResource(context.resources, R.mipmap.transit))

                                val symbolManager = SymbolManager(mapView, mapLibreMap, style).apply {
                                    iconAllowOverlap = true
                                    textAllowOverlap = true
                                }

                                val cameraLocation = if (routeResponse.isNullOrEmpty()) {
                                    location
                                } else {
                                    initialLocation ?: location
                                }
                                mapLibreMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraLocation, 18.0))



                                initialLocation?.let {
                                    val initialSymbolOptions = SymbolOptions()
                                        .withLatLng(it)
                                        .withIconImage("initial-icon")
                                        .withTextField("Start")
                                        .withTextOffset(arrayOf(0f, 1.5f))
                                    symbolManager.create(initialSymbolOptions)
                                }

                                destinationLocation?.let {
                                    val destinationSymbolOptions = SymbolOptions()
                                        .withLatLng(it)
                                        .withIconImage("destination-icon")
                                        .withTextField("Destination")
                                        .withTextOffset(arrayOf(0f, 1.5f))
                                    symbolManager.create(destinationSymbolOptions)
                                }
                                routeResponse?.forEachIndexed { index, (route, color) ->
                                    val coordinates = route.routes.first().legs.flatMap { leg ->
                                        leg.steps.flatMap { step ->
                                            PolylineUtils.decode(step.geometry, 5)
                                        }
                                    }.map {
                                        Point.fromLngLat(it.longitude(), it.latitude())
                                    }

                                    val duration = route.routes.first().duration / 60 // convert to minutes
                                    val distance = route.routes.first().distance / 1000 // convert to kilometers
                                    val midpoint = coordinates[coordinates.size / 2]
                                    Log.d("OSMMainMap",coordinates.toString())
                                    val iconName = when (color) {
                                        "#0000FF" -> "walking-icon" // Blue for walking
                                        "#FFA500" -> "cycling-icon" // Orange for cycling
                                        "#FF0000" -> "car-icon" // Red for car
                                        "#00FF00" -> "transit-icon" // Green for transit
                                        else -> "default-icon"
                                    }
                                    Log.d("OSMMainMap",color.toString())
                                    Log.d("OSMMainMap", "Adding symbol at $midpoint with icon $iconName and text ${duration}min, ${distance.format(2)} km")
                                    symbolManager.create(
                                        SymbolOptions()
                                        .withLatLng(LatLng(midpoint.latitude(), midpoint.longitude()))
                                        .withIconImage(iconName)
                                        .withTextField("${duration.toInt()} min\n${distance.format(2)} km")
                                        .withTextOffset(arrayOf(0f, 1.5f))
                                    )


                                    val routeFeature = Feature.fromGeometry(LineString.fromLngLats(coordinates))
                                    val routeSource = GeoJsonSource("route-source-$index", FeatureCollection.fromFeature(routeFeature))
                                    style.addSource(routeSource)

                                    val routeLayer = LineLayer("route-layer-$index", "route-source-$index").apply {
                                        setProperties(
                                            lineColor(Color.parseColor(color)),
                                            lineWidth(5.0f)
                                        )
                                    }
                                    style.addLayer(routeLayer)
                                }
                            }
                        }
                    }.also { mapViewState.value = it }
                },

                update = { view ->
                    view.onStart()
                    view.onResume()
                }
            )
        } ?: run {
            // Create the map view if it's null
            mapViewState.value = MapView(context)
        }
    } else {
        if (permissionsState.shouldShowRationale) {
            AlertDialog(
                onDismissRequest = { /* Do nothing */ },
                title = { Text("Permissions Required") },
                text = { Text("This app requires location permissions to show your position on the map.") },
                confirmButton = {
                    TextButton(onClick = { permissionsState.launchMultiplePermissionRequest() }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

private fun Double.format(digits: Int) = "%.${digits}f".format(this)