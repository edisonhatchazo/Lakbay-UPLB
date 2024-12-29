package com.edison.lakbayuplb.ui.map

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edison.lakbayuplb.R
import com.edison.lakbayuplb.ui.navigation.AppViewModelProvider
import com.edison.lakbayuplb.ui.screen.MapScreenTopAppBar
import com.edison.lakbayuplb.ui.settings.global.RouteSettingsViewModel
import com.edison.lakbayuplb.ui.settings.global.TopAppBarColorSchemesViewModel
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

@Composable
fun MainMapScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
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
    val userCurrentLocation = GeoPoint(14.167033493867187, 121.24304070072948)
    val routeResponse by viewModel.routeResponse.observeAsState()
    var selectedRouteType by rememberSaveable  { mutableStateOf("foot") }
    var isCalculatingRoute by remember {mutableStateOf(false)}
    val isDoubleTransit = routeViewModel.forestryRouteDoubleRideEnabled.collectAsState().value
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

                    val startLat = (initialLocation ?: userCurrentLocation).latitude
                    val startLng = (initialLocation ?: userCurrentLocation).longitude
                    val endLat = destinationLocation?.latitude
                    val endLng = destinationLocation?.longitude

                    if (endLat != null && endLng != null) {
                        coroutineScope.launch {
                            isCalculatingRoute = true
                            viewModel.calculateRouteFromUserInput(
                                profile = selectedRouteType,
                                startLat = startLat,
                                startLng = startLng,
                                endLat = endLat,
                                endLng = endLng,
                                doubleTransit = isDoubleTransit
                            )
                            isCalculatingRoute = false
                        }
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
                    coroutineScope.launch {
                        viewModel.calculateRouteFromUserInput(
                            profile = selectedRouteType, // This can be dynamic
                            startLat = initialLocation!!.latitude,
                            startLng = initialLocation!!.longitude,
                            endLat = destinationLocation!!.latitude,
                            endLng = destinationLocation!!.longitude,
                            doubleTransit = isDoubleTransit
                        )
                    }
                }

            )
        }

        if (isCalculatingRoute) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("Calculating Route") },
                text = {
                    Text(
                        if(selectedRouteType == "transit") {
                            if (isDoubleTransit) "Calculating the double transit route..."
                            else "Calculating the transit route..."
                        }else{
                            "Calculating the $selectedRouteType route..."
                        }
                    )
                },
                confirmButton = {}
            )
        }

        val counter = mutableListOf(0)

        GuideMapDetails(
            initialLocation = initialLocation,
            routeType = selectedRouteType,
            destinationLocation = destinationLocation,
            title = "Destination",
            snippet = "",
            navigateBack = navigateBack,
            routeResponse = routeResponse,
            counter = counter,
            modifier = Modifier.padding(0.dp),
            contentPadding = innerPadding,
        )
    }
}