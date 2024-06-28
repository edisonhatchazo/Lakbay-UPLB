package com.example.classschedule.ui.screen

import android.Manifest
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.example.classschedule.ui.theme.ClassScheduleTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

enum class CustomMapType(val displayName: String, val mapType: MapType) {
    NORMAL("Normal", MapType.NORMAL),
    SATELLITE("Satellite", MapType.SATELLITE),
    TERRAIN("Terrain", MapType.TERRAIN),
    HYBRID("Hybrid", MapType.HYBRID)
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(title: String, latitude: Double, longitude: Double) {
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
        ClassScheduleTheme {
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                val location = LatLng(latitude, longitude)
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(location, 18f)
                }
                val markerState = rememberMarkerState(position = location)

                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState
                ) {
                    Marker(
                        state = markerState,
                        title = title,
                        snippet = title
                    )
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