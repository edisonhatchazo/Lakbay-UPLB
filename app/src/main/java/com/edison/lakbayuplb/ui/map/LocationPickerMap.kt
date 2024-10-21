package com.edison.lakbayuplb.ui.map

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.edison.lakbayuplb.R
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker


@Composable
fun LocationPickerMap(
    latitude: Double,
    longitude: Double,
    pinTitle: String,
    onMapClick: (Double, Double) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var selectedLocation by remember { mutableStateOf(GeoPoint(latitude, longitude)) }

    DisposableEffect(Unit) {
        mapView = MapView(context).apply {
            // Initialize OSMDroid configuration
            Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid_preferences", Context.MODE_PRIVATE))
            setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
            setMultiTouchControls(true)

            // Set initial map center and zoom level
            controller.setCenter(GeoPoint(latitude, longitude))
            controller.setZoom(18.0)

            // Add a tap listener to capture user clicks
            overlays.add(MapEventsOverlay(object : MapEventsReceiver {
                override fun singleTapConfirmedHelper(geoPoint: GeoPoint): Boolean {
                    // Update selected location on map click
                    selectedLocation = geoPoint
                    onMapClick(geoPoint.latitude, geoPoint.longitude)
                    return true
                }

                override fun longPressHelper(geoPoint: GeoPoint): Boolean {
                    return false
                }
            }))

            // Add marker to indicate selected location
            val marker = Marker(this)
            marker.position = selectedLocation
            marker.title = pinTitle
            overlays.add(marker)
        }

        onDispose {
            mapView?.onPause()
            mapView?.onDetach()
            mapView = null
        }
    }

    mapView?.let { view ->
        LaunchedEffect(view) {
            // Update marker when selected location changes
            val marker = Marker(view).apply {
                position = selectedLocation
                title = pinTitle
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            }

            // Add the marker to the map
            view.overlays.add(marker)
        }

        // Layout the map view in the Composable
        Column {
            AndroidView(
                factory = { view },
                modifier = Modifier.weight(1f)
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
}

@Composable
fun MapPointPickerDialog(
    title: String,
    startCoordinates: GeoPoint?,
    destinationCoordinates: GeoPoint?,
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

                Text("Start: ${startCoordinates?.latitude}, ${startCoordinates?.longitude}")
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
                        Text("Choose Start Location")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Destination: ${destinationCoordinates?.latitude}, ${destinationCoordinates?.longitude}")
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
                        Text("Choose Destination Location")
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
                        Text("Confirm")
                    }
                }

                if (showMapDialog) {
                    val pinTitle = if (isSelectingStartPoint) "Start" else "Destination"
                    LocationPickerMap(
                        latitude = 14.16747822735461,
                        longitude = 121.24338486047947,
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

@Composable
fun GuideMapPointPickerDialog(
    title: String,
    startCoordinates: GeoPoint?,
    isOnline: Boolean,
    isLocationEnabled: Boolean,
    onDismissRequest: () -> Unit,
    onStartPointSelected: (Double, Double) -> Unit,
    onConfirm: () -> Unit,
    onGetCurrentLocation: () -> Unit
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

                if(!isOnline){
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Device is Offline")
                    Spacer(modifier = Modifier.height(8.dp))
                }else{
                    if (!isLocationEnabled) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Location is Disabled")
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            onGetCurrentLocation()
                            onConfirm()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        enabled = isOnline && isLocationEnabled
                    ) {
                        Text(stringResource(R.string.current_location))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            isSelectingStartPoint = true
                            showMapDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        enabled = isOnline
                    ) {
                        Text(stringResource(R.string.choose_start_location))
                    }
                }


                Spacer(modifier = Modifier.height(16.dp))


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = onConfirm,
                        enabled = startCoordinates != null
                    ) {
                        Text(stringResource(R.string.confirm))
                    }
                }

                if (showMapDialog) {
                    val pinTitle = if (isSelectingStartPoint) "Start" else "Destination"
                    LocationPickerMap(
                        latitude = 14.16747822735461,
                        longitude = 121.24338486047947,
                        pinTitle = pinTitle,
                        onMapClick = { lat, lng ->

                            onStartPointSelected(lat, lng)

                            showMapDialog = false
                        }
                    )
                }
            }
        }
    }
}