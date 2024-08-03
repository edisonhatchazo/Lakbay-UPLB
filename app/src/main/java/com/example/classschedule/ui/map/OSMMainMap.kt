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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.classschedule.BuildConfig
import com.example.classschedule.R
import com.example.classschedule.algorithm.transit.RouteWithLineString
import com.example.classschedule.ui.settings.global.RouteSettingsViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapView
import org.maplibre.android.plugins.annotation.Symbol
import org.maplibre.android.plugins.annotation.SymbolManager
import org.maplibre.android.plugins.annotation.SymbolOptions
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.LineString
import org.maplibre.geojson.Point
import org.maplibre.geojson.utils.PolylineUtils
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin


enum class OSMCustomMapType(val styleUrl: String) {
    STREET("${BuildConfig.MAP_API_BASE_URL}/styles/streets/style.json"),
    BRIGHT("${BuildConfig.MAP_API_BASE_URL}/styles/basic/style.json"),
    DARK_MODE("${BuildConfig.MAP_API_BASE_URL}/styles/dark_mode/style.json"),
    OSM_3D("${BuildConfig.MAP_API_BASE_URL}/styles/3D/style.json")
}



@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun OSMMainMap(
    title: String?,
    location: LatLng,
    initialLocation: LatLng?,
    routeType:String,
    routeViewModel: RouteSettingsViewModel,
    destinationLocation: LatLng?,
    routeResponse: List<RouteWithLineString>?,
    styleUrl: String,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    val permissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val walkingSpeed by routeViewModel.walkingSpeed.collectAsState()
    val cyclingSpeed by routeViewModel.cyclingSpeed.collectAsState()
    val carSpeed by routeViewModel.carSpeed.collectAsState()
    val jeepneySpeed by routeViewModel.jeepneySpeed.collectAsState()
    val selectedSpeed = when (routeType) {
        "foot" -> walkingSpeed
        "bicycle" -> cyclingSpeed
        "car" -> carSpeed
        "transit" -> jeepneySpeed
        else -> carSpeed // Default to car speed if route type is unknown
    }

    val minZoom = 14.0
    val maxZoom = 19.0
    val bounds = LatLngBounds.Builder()
        .include(LatLng(14.116059432252356,  121.29498816252921)) // Southwest corner
        .include(LatLng(14.18336407476095, 121.19205689274669)) // Northeast corner
        .build()

    var mapViewKey by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        permissionsState.launchMultiplePermissionRequest()
    }

    if (permissionsState.allPermissionsGranted) {
        var mapView by remember { mutableStateOf<MapView?>(null) }
        var symbolManager by remember { mutableStateOf<SymbolManager?>(null) }
        var symbols by remember { mutableStateOf<List<Symbol>>(emptyList()) }

        MapLibre.getInstance(context)
         DisposableEffect(Unit) {
            onDispose {
                mapView?.onStop()
                mapView?.onDestroy()
                mapView = null
                symbolManager?.let {
                    it.delete(symbols)
                    it.onDestroy()
                }
                symbols = emptyList()
                symbolManager = null
            }
        }

        LaunchedEffect(key1 = routeResponse,key2 = styleUrl) {
            symbolManager?.let {
                it.delete(symbols)
                symbols = emptyList()
                mapViewKey++ // Force map view reset
            }
            routeResponse?.forEach { routeWithLineString ->
                if(routeWithLineString.lineString!="") {
                    Log.d("OSMMainMap", "Bus Route Name: ${routeWithLineString.lineString}")
                }
            }
        }

        LaunchedEffect(mapViewKey) {
            mapView?.getMapAsync { mapLibreMap ->
                mapLibreMap.setStyle(styleUrl) { style ->

                    val manager = SymbolManager(mapView!!, mapLibreMap, style).apply {
                        iconAllowOverlap = true
                        textAllowOverlap = true
                    }
                    symbolManager = manager

                    style.addImage("destination-icon", BitmapFactory.decodeResource(context.resources, R.drawable.marker_48))
                    style.addImage("initial-icon", BitmapFactory.decodeResource(context.resources, R.drawable.icons8_circle_18___))
                    style.addImage("walking-icon", BitmapFactory.decodeResource(context.resources, R.drawable.walking_icon))
                    style.addImage("cycling-icon", BitmapFactory.decodeResource(context.resources, R.drawable.cycling_icon))
                    style.addImage("car-icon", BitmapFactory.decodeResource(context.resources, R.drawable.car_icon))
                    style.addImage("transit-icon", BitmapFactory.decodeResource(context.resources, R.mipmap.transit))

                    mapLibreMap.setMinZoomPreference(minZoom)
                    mapLibreMap.setMaxZoomPreference(maxZoom)
                    mapLibreMap.setLatLngBoundsForCameraTarget(bounds)
                    val cameraLocation = if (routeResponse.isNullOrEmpty()) {
                        location
                    } else {
                        initialLocation ?: location
                    }
                    mapLibreMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraLocation, 18.0))

                    initialLocation?.let {
                        val bearing = destinationLocation?.let { dest ->
                            calculateBearing(it, dest)
                        } ?: 0f
                        mapLibreMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 18.0))
                        mapLibreMap.animateCamera(CameraUpdateFactory.bearingTo(bearing.toDouble()))
                    }

                    if(initialLocation != null && destinationLocation != null) {
                        symbols = listOf(
                            manager.create(
                                SymbolOptions()
                                    .withLatLng(destinationLocation)
                                    .withIconImage("destination-icon")
                                    .withTextField(title)
                                    .withTextOffset(arrayOf(0f, 1.5f))
                            ),
                            manager.create(
                                SymbolOptions()
                                    .withLatLng(initialLocation)
                                    .withIconImage("initial-icon")
                                    .withTextField("Your Location")
                                    .withTextOffset(arrayOf(0f, 1.5f))
                            )
                        )
                    }

                    routeResponse?.forEachIndexed { index, (route, color) ->
                        val coordinates = route.routes.first().legs.flatMap { leg ->
                            leg.steps.flatMap { step ->
                                PolylineUtils.decode(step.geometry, 5)
                            }
                        }.map {
                            Point.fromLngLat(it.longitude(), it.latitude())
                        }

                        val distance = route.routes.first().distance // in meters
                        val duration = distance / selectedSpeed / 60 // in minutes

                        val midpoint = coordinates[coordinates.size / 2]
                        val adjustedMidpoint = LatLng(midpoint.latitude() + 0.0002, midpoint.longitude() - 0.0002)

                        val iconName = when (color) {
                            "#0000FF" -> "walking-icon" // Blue for walking
                            "#FFA500" -> "cycling-icon" // Orange for cycling
                            "#FF0000" -> "car-icon" // Red for car
                            "#00FF00" -> "transit-icon" // Green for transit
                            else -> "default-icon"
                        }


                        symbols += manager.create(
                            SymbolOptions()
                                .withLatLng(adjustedMidpoint)
                                .withIconImage(iconName)
                                .withTextField("${duration.toInt()} min\n${distance.format(2)} m")
                                .withTextOffset(arrayOf(0f, 1.5f))
                                .withTextJustify("auto")
                                .withTextAnchor("top")
                        )

                        val routeFeature = Feature.fromGeometry(LineString.fromLngLats(coordinates))
                        val routeSource = GeoJsonSource("route-source-$index", FeatureCollection.fromFeature(routeFeature))
                        style.addSource(routeSource)

                        val routeLayer = LineLayer("route-layer-$index", "route-source-$index").apply {
                            setProperties(
                                PropertyFactory.lineColor(Color.parseColor(color)),
                                PropertyFactory.lineWidth(5.0f)
                            )
                        }
                        style.addLayer(routeLayer)

                    }


                }
            }
        }

        AndroidView(
            modifier = modifier.fillMaxSize(),
            factory = {
                MapView(context).apply {
                    mapView = this
                }
            },
            update = {
                mapView = it
            }
        )
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

fun Double.format(digits: Int) = "%.${digits}f".format(this)

fun calculateBearing(start: LatLng, end: LatLng): Double {
    val lat1 = Math.toRadians(start.latitude)
    val lon1 = Math.toRadians(start.longitude)
    val lat2 = Math.toRadians(end.latitude)
    val lon2 = Math.toRadians(end.longitude)

    val dLon = lon2 - lon1
    val y = sin(dLon) * cos(lat2)
    val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)
    val bearing = Math.toDegrees(atan2(y, x))

    return ((bearing + 360) % 360)
}
