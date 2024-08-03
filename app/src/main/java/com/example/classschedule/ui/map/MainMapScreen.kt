package com.example.classschedule.ui.map

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.classschedule.R
import com.example.classschedule.algorithm.transit.RouteWithLineString
import com.example.classschedule.ui.navigation.AppViewModelProvider
import com.example.classschedule.ui.screen.MapScreenTopAppBar
import com.example.classschedule.ui.settings.global.RouteSettingsViewModel
import org.maplibre.android.geometry.LatLng

@Composable
fun MainMapScreen(
    modifier: Modifier = Modifier,
    openDrawer: () -> Unit,
    viewModel: MapViewModel = viewModel(factory = AppViewModelProvider.Factory),
    routeViewModel: RouteSettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    var showMapDialog by remember { mutableStateOf(false) }
    var initialLocation by remember { mutableStateOf<LatLng?>(null) }
    var destinationLocation by remember { mutableStateOf<LatLng?>(null) }
    val routeResponse by viewModel.routeResponse.observeAsState()
    var selectedRouteType by remember { mutableStateOf("foot") }
    var isCalculatingRoute = viewModel.isCalculatingRoute
    var styleUrl by remember { mutableStateOf(OSMCustomMapType.OSM_3D.styleUrl) }
    val isDoubleTransit = routeViewModel.forestryRouteDoubleRideEnabled.collectAsState().value

    Scaffold(
        modifier = modifier,
        topBar = {
            MapScreenTopAppBar(
                title = stringResource(R.string.map),
                onGetDirectionsClick = { showMapDialog = true },
                openDrawer = openDrawer,
                onRouteTypeSelected = { routeType ->
                    selectedRouteType = routeType
                    if (initialLocation != null && destinationLocation != null) {
                        viewModel.calculateRouteFromUserInput(
                            routeType,
                            initialLocation!!.latitude,
                            initialLocation!!.longitude,
                            destinationLocation!!.latitude,
                            destinationLocation!!.longitude
                        )
                    }
                },
                onMapTypeSelected = {mapType ->
                    styleUrl = mapType.styleUrl
                }
            )
        }
    ){ innerPadding ->
        if (showMapDialog) {
            MapPointPickerDialog(
                title = "Get Directions",
                startCoordinates = initialLocation,
                destinationCoordinates = destinationLocation,
                onDismissRequest = { showMapDialog = false },
                onStartPointSelected = { lat, lng ->
                    initialLocation = LatLng(lat, lng)
                },
                onDestinationPointSelected = { lat, lng ->
                    destinationLocation = LatLng(lat, lng)
                },
                onConfirm = {
                    // Reset the map state and trigger route calculation
                    showMapDialog = false
                    viewModel.calculateRouteFromUserInput(
                        selectedRouteType, // This can be dynamic
                        initialLocation!!.latitude,
                        initialLocation!!.longitude,
                        destinationLocation!!.latitude,
                        destinationLocation!!.longitude
                    )
                }

            )
        }

        if (isCalculatingRoute) {
            AlertDialog(
                onDismissRequest = {},
                title = {Text("Calculating Route")},
                text = {
                    Text(
                        if (isDoubleTransit) "Calculating Double Transit Route..."
                        else "Calculating the Transit Route..."
                    )
               },
                confirmButton = {}
            )
        }

        LaunchedEffect(key1 = routeResponse, selectedRouteType) {
            if (routeResponse != null) {
                isCalculatingRoute = false
            }
        }


        MapDetails(
            initialLocation = initialLocation,
            routeType = selectedRouteType,
            destinationLocation = destinationLocation,
            routeResponse = routeResponse,
            routeViewModel = routeViewModel,
            modifier = Modifier.padding(0.dp),
            contentPadding = innerPadding,
            styleUrl = styleUrl
        )


    }
}

@Composable
fun MapDetails(
    initialLocation: LatLng?,
    destinationLocation: LatLng?,
    routeViewModel: RouteSettingsViewModel,
    routeType: String,
    routeResponse: List<RouteWithLineString>?,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    styleUrl: String
) {
    val coordinates= LatLng(14.165008914904659, 121.24150742562976)


    OSMMainMap(
        title = "Destination",
        location = coordinates,
        initialLocation = initialLocation,
        routeType = routeType,
        routeViewModel = routeViewModel,
        destinationLocation = destinationLocation,
        routeResponse = routeResponse,
        styleUrl = styleUrl,
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
    )
}

