package com.example.classschedule.ui.map

import android.Manifest
import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.classschedule.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.plugins.annotation.SymbolManager
import org.maplibre.android.plugins.annotation.SymbolOptions

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun OSMDetailsMapping(title: String, latitude: Double, longitude: Double, styleUrl: String) {
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

    if (permissionsState.allPermissionsGranted && title.isNotEmpty()) {
        var mapView by remember { mutableStateOf<MapView?>(null) }

        DisposableEffect(Unit) {
            MapLibre.getInstance(context, apiKey, tileServer)
            mapView = MapView(context)

            onDispose {
                mapView?.onStop()
                mapView?.onDestroy()
                mapView = null
            }
        }

        mapView?.let { view ->
            LaunchedEffect(view) {
                view.getMapAsync { mapLibreMap ->
                    val location = LatLng(latitude, longitude)

                    mapLibreMap.setStyle(styleUrl) { style ->
                        mapLibreMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 18.0))

                        // Add the marker icon to the style
                        style.addImage("marker-icon", BitmapFactory.decodeResource(context.resources, R.drawable.marker_48))

                        // Initialize SymbolManager
                        val symbolManager = SymbolManager(view, mapLibreMap, style).apply {
                            iconAllowOverlap = true
                            textAllowOverlap = true
                        }

                        // Add a marker (symbol)
                        val symbolOptions = SymbolOptions()
                            .withLatLng(location)
                            .withIconImage("marker-icon")
                            .withTextField(title)
                            .withTextOffset(arrayOf(0f, 1.5f))
                        symbolManager.create(symbolOptions)
                    }
                }
            }

            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { view }
            )
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