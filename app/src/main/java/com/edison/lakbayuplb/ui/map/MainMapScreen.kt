package com.edison.lakbayuplb.ui.map

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edison.lakbayuplb.R
import com.edison.lakbayuplb.algorithm.routing_algorithm.RouteWithLineString
import com.edison.lakbayuplb.ui.navigation.AppViewModelProvider
import com.edison.lakbayuplb.ui.screen.MapScreenTopAppBar
import com.edison.lakbayuplb.ui.settings.global.RouteSettingsViewModel
import com.edison.lakbayuplb.ui.settings.global.TopAppBarColorSchemesViewModel
import org.osmdroid.util.GeoPoint

@Composable
fun MainMapScreen(
    modifier: Modifier = Modifier,
    openDrawer: () -> Unit,
    viewModel: MapViewModel = viewModel(factory = AppViewModelProvider.Factory),
    routeViewModel: RouteSettingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    colorViewModel: TopAppBarColorSchemesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val topAppBarColors = colorViewModel.topAppBarColors.collectAsState()
    val (topAppBarBackgroundColor, topAppBarForegroundColor) = topAppBarColors.value

    var showMapDialog by remember { mutableStateOf(false) }
    var initialLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var destinationLocation by remember { mutableStateOf<GeoPoint?>(null) }
    val routeResponse by viewModel.routeResponse.observeAsState()
    var selectedRouteType by remember { mutableStateOf("foot") }
    var isCalculatingRoute = viewModel.isCalculatingRoute
    val isDoubleTransit = routeViewModel.forestryRouteDoubleRideEnabled.collectAsState().value

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

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
                            profile = selectedRouteType, // This can be dynamic
                            startLat = initialLocation!!.latitude,
                            startLng = initialLocation!!.longitude,
                            endLat =destinationLocation!!.latitude,
                            endLng = destinationLocation!!.longitude,
                            doubleTransit = isDoubleTransit,
                            colorCode = when (selectedRouteType) {
                            "foot" -> "#00FF00"  // Green for foot
                            "bicycle" -> "#0000FF"  // Blue for cycling
                            "driving" -> "#FF0000"  // Red for driving
                            else -> "#000000"  // Black for any other type
                            }
                        )
                    }
                },
                topAppBarBackgroundColor = topAppBarBackgroundColor,
                topAppBarForegroundColor = topAppBarForegroundColor
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
                    initialLocation = GeoPoint(lat, lng)
                },
                onDestinationPointSelected = { lat, lng ->
                    destinationLocation = GeoPoint(lat, lng)
                },
                onConfirm = {
                    // Reset the map state and trigger route calculation
                    showMapDialog = false
                    viewModel.calculateRouteFromUserInput(
                        profile = selectedRouteType, // This can be dynamic
                        startLat = initialLocation!!.latitude,
                        startLng = initialLocation!!.longitude,
                        endLat =destinationLocation!!.latitude,
                        endLng = destinationLocation!!.longitude,
                        doubleTransit = isDoubleTransit,
                        colorCode = when (selectedRouteType) {
                            "foot" -> "#00FF00"  // Green for foot
                            "bicycle" -> "#0000FF"  // Blue for cycling
                            "driving" -> "#FF0000"  // Red for driving
                            else -> "#000000"  // Black for any other type
                        },
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
        )


    }
}

@Composable
fun MapDetails(
    initialLocation: GeoPoint?,
    destinationLocation: GeoPoint?,
    routeViewModel: RouteSettingsViewModel,
    routeType: String,
    routeResponse: List<RouteWithLineString>?,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    val title = "UPLB"
    val snippet = "2nd Floor"
    OSMMapView(modifier,title,snippet,initialLocation,routeType,routeResponse,destinationLocation)
}

