package com.edison.lakbayuplb.algorithm

import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker


@Composable
fun MapSelectionDialog(
    initialLocation: GeoPoint,
    onLocationSelected: (GeoPoint) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedLocation by remember { mutableStateOf(initialLocation) }
    val context = LocalContext.current
    var mapView by remember { mutableStateOf<MapView?>(null) }

    // Define the bounding box limits
    val boundingBox = BoundingBox(
        14.178379933496627, 121.25898739159179,
        14.147189880033771, 121.22749500166606
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Location") },
        text = {
            AndroidView(
                factory = {
                    MapView(context).apply {
                        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid_preferences", Context.MODE_PRIVATE))
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)

                        // Set initial map center and zoom level
                        controller.setCenter(initialLocation)
                        controller.setZoom(18.0)

                        // Set min and max zoom levels
                        minZoomLevel = 17.0
                        maxZoomLevel = 21.0

                        // Limit the scrollable area to the bounding box
                        setScrollableAreaLimitDouble(boundingBox)

                        // Add a tap listener to capture user clicks
                        overlays.add(MapEventsOverlay(object : MapEventsReceiver {
                            override fun singleTapConfirmedHelper(geoPoint: GeoPoint): Boolean {
                                // Update the selected location when the map is clicked
                                selectedLocation = geoPoint

                                // Clear previous markers and add a new marker at the clicked location
                                this@apply.overlays.removeAll { it is Marker } // Remove the existing marker
                                val marker = Marker(this@apply).apply {
                                    position = geoPoint
                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                }
                                this@apply.overlays.add(marker)
                                this@apply.invalidate() // Refresh the map to show the updated marker
                                return true
                            }

                            override fun longPressHelper(geoPoint: GeoPoint): Boolean {
                                return false
                            }
                        }))

                        // Add the initial marker at the initial location
                        val marker = Marker(this).apply {
                            position = initialLocation
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        }
                        overlays.add(marker)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        },
        confirmButton = {
            Button(onClick = {
                onLocationSelected(selectedLocation)
            }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
