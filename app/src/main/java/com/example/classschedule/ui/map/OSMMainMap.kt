package com.example.classschedule.ui.map

import android.Manifest
import android.graphics.BitmapFactory
import android.graphics.Color
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
import com.example.classschedule.R
import com.example.classschedule.algorithm.osrms.RouteResponse
import com.example.classschedule.ui.settings.global.RouteViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
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


enum class OSMCustomMapType( val styleUrl: String) {
    STREET("https://api.maptiler.com/maps/streets-v2/style.json?key=w30aQM8FugoPfybqIZz7"),
    OSM("https://api.maptiler.com/maps/openstreetmap/style.json?key=w30aQM8FugoPfybqIZz7"),
    SATELLITE("https://api.maptiler.com/maps/satellite/style.json?key=w30aQM8FugoPfybqIZz7"),
    LANDSCAPE( "https://api.maptiler.com/maps/landscape/style.json?key=w30aQM8FugoPfybqIZz7")
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun OSMMainMap(
    title: String?,
    location: LatLng,
    initialLocation: LatLng?,
    routeType:String,
    routeViewModel: RouteViewModel,
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

    var mapViewKey by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        permissionsState.launchMultiplePermissionRequest()
    }

    if (permissionsState.allPermissionsGranted) {
        var mapView by remember { mutableStateOf<MapView?>(null) }
        var symbolManager by remember { mutableStateOf<SymbolManager?>(null) }
        var symbols by remember { mutableStateOf<List<Symbol>>(emptyList()) }

        MapLibre.getInstance(context, apiKey, tileServer)

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

        LaunchedEffect(routeResponse) {
            symbolManager?.let {
                it.delete(symbols)
                symbols = emptyList()
                mapViewKey++ // Force map view reset
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

                    val cameraLocation = if (routeResponse.isNullOrEmpty()) {
                        location
                    } else {
                        initialLocation ?: location
                    }
                    mapLibreMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraLocation, 18.0))


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
            modifier = Modifier.fillMaxSize(),
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