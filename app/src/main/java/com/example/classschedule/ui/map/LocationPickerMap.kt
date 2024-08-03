package com.example.classschedule.ui.map

import android.Manifest
import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.example.classschedule.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.plugins.annotation.SymbolManager
import org.maplibre.android.plugins.annotation.SymbolOptions

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPickerMap(
    latitude: Double,
    longitude: Double,
    styleUrl: String,
    pinTitle: String,
    onMapClick: (Double, Double) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val permissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    var selectedLocation by remember { mutableStateOf(LatLng(latitude, longitude)) }

    LaunchedEffect(Unit) {
        permissionsState.launchMultiplePermissionRequest()
    }

    if (permissionsState.allPermissionsGranted) {
        var mapView by remember { mutableStateOf<MapView?>(null) }

        MapLibre.getInstance(context)
        DisposableEffect(Unit) {
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
                        // Add initial pin marker
                        var pinSymbol = symbolManager.create(
                            SymbolOptions()
                                .withLatLng(selectedLocation)
                                .withIconImage("marker-icon")
                                .withTextField(pinTitle)
                                .withTextOffset(arrayOf(0f, 1.5f))
                        )

                        mapLibreMap.addOnMapClickListener { point ->
                            selectedLocation = LatLng(point.latitude, point.longitude)
                            pinSymbol.latLng = selectedLocation
                            symbolManager.update(pinSymbol)
                            true
                        }
                    }
                }
            }

            Column {
                AndroidView(
                    modifier = Modifier.weight(1f),
                    factory = { view }
                )
                Button(
                    onClick = { onMapClick(selectedLocation.latitude, selectedLocation.longitude) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Confirm Location")
                }
            }
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

@Composable
fun MapPointPickerDialog(
    title: String,
    startCoordinates: LatLng?,
    destinationCoordinates: LatLng?,
    onDismissRequest: () -> Unit,
    onStartPointSelected: (Double, Double) -> Unit,
    onDestinationPointSelected: (Double, Double) -> Unit,
    onConfirm: () -> Unit
) {
    var showMapDialog by remember { mutableStateOf(false) }
    var isSelectingStartPoint by remember { mutableStateOf(true) }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("$title:")

                Spacer(modifier = Modifier.height(8.dp))

                Text(stringResource(R.string.start) + ": ${startCoordinates?.latitude}, ${startCoordinates?.longitude}")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            isSelectingStartPoint = true
                            showMapDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text(stringResource(R.string.choose_start_location))
                    }
                }


                Spacer(modifier = Modifier.height(16.dp))

                Text(stringResource(R.string.destination) + ": ${destinationCoordinates?.latitude}, ${destinationCoordinates?.longitude}")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            isSelectingStartPoint = false
                            showMapDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text(stringResource(R.string.choose_destination_location))
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = onConfirm,
                        enabled = startCoordinates != null && destinationCoordinates != null
                    ) {
                        Text(stringResource(R.string.confirm))
                    }
                }

                if (showMapDialog) {
                    val pinTitle = if (isSelectingStartPoint) "Start" else "Destination"
                    LocationPickerMap(
                        latitude = 14.16747822735461,
                        longitude = 121.24338486047947,
                        styleUrl = OSMCustomMapType.OSM_3D.styleUrl,
                        pinTitle = pinTitle,
                        onMapClick = { lat, lng ->
                            if (isSelectingStartPoint) {
                                onStartPointSelected(lat, lng)
                            } else {
                                onDestinationPointSelected(lat, lng)
                            }
                            showMapDialog = false
                        }
                    )
                }
            }
        }
    }
}