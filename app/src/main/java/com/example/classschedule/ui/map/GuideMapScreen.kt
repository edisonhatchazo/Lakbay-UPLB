package com.example.classschedule.ui.map

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.classschedule.R
import com.example.classschedule.algorithm.osrms.isLocationEnabled
import com.example.classschedule.algorithm.osrms.isOnline
import com.example.classschedule.algorithm.transit.RouteWithLineString
import com.example.classschedule.ui.navigation.AppViewModelProvider
import com.example.classschedule.ui.navigation.NavigationDestination
import com.example.classschedule.ui.screen.GuideScreenTopAppBar
import com.example.classschedule.ui.settings.global.RouteSettingsViewModel
import org.maplibre.android.geometry.LatLng

object GuideMapDestination: NavigationDestination {
    override val route = "guide_map"
    override val titleRes = R.string.map

    const val MAPDATAIDARG = "mapId"
    val routeWithArgs = "$route/{$MAPDATAIDARG}"
}


@Composable
fun GuideMapScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = viewModel(factory = AppViewModelProvider.Factory),
    locationViewModel: LocationViewModel = viewModel(factory = AppViewModelProvider.Factory),
    routeViewModel: RouteSettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val context = LocalContext.current
    val uiState by locationViewModel.uiState.collectAsState()
    val maps = uiState.mapDataDetails
    val currentLocation by locationViewModel.currentLocation.observeAsState(null)
    val destinationLocation = LatLng(maps.latitude, maps.longitude)
    val routeResponse by viewModel.routeResponse.observeAsState()
    var selectedRouteType by remember { mutableStateOf("foot") }

    var styleUrl by remember { mutableStateOf(OSMCustomMapType.OSM_3D) }
    var isDoubleTransit = routeViewModel.forestryRouteDoubleRideEnabled.collectAsState().value

    // Check network and location status
    val isOnline = isOnline(context)
    val isLocationEnabled = isLocationEnabled(context)

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                locationViewModel.getCurrentLocationOnce()
            } else {
                println("Location permission denied")
            }
        }
    )

    DisposableEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED) {
            locationViewModel.getCurrentLocationOnce()
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        onDispose {
            locationViewModel.stopLocationUpdates()
        }
    }

    val initialLocation = when {
        isOnline && isLocationEnabled -> currentLocation ?: LatLng(14.165008914904659, 121.24150742562976) // Default if currentLocation is null
        isOnline && !isLocationEnabled -> LatLng(14.165008914904659, 121.24150742562976) // Set to initial location
        !isOnline -> null // Set to null if offline
        else -> LatLng(14.165008914904659, 121.24150742562976) // Fallback initial location
    }

    LaunchedEffect(selectedRouteType, initialLocation, destinationLocation) {
        initialLocation?.let {
            viewModel.calculateRouteFromUserInput(
                selectedRouteType,
                it.latitude,
                it.longitude,
                destinationLocation.latitude,
                destinationLocation.longitude
            )
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            GuideScreenTopAppBar(
                title = "Guide to Destination",
                canNavigateBack = true,
                navigateUp = navigateBack,
                onRouteTypeSelected = { routeType ->
                    selectedRouteType = routeType
                    if (isOnline) {
                        initialLocation?.let {
                            viewModel.calculateRouteFromUserInput(
                                routeType,
                                it.latitude,
                                it.longitude,
                                destinationLocation.latitude,
                                destinationLocation.longitude
                            )
                        }
                    }
                    if (routeType == "transit") {
                        viewModel.loadAllBusStops()
                    }
                },
                onMapTypeSelected = { mapType ->
                    styleUrl = mapType
                }
            )
        }
    ) { innerPadding ->
        GuideMapDetails(
            initialLocation = initialLocation,
            destinationLocation = destinationLocation,
            title = maps.title,
            routeType = selectedRouteType,
            routeViewModel = routeViewModel,
            snippet = maps.snippet,
            styleUrl = styleUrl,
            routeResponse = routeResponse,
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                )
        )
    }
}

@Composable
fun GuideMapDetails(
    initialLocation: LatLng?,
    title: String,
    snippet: String,
    routeViewModel: RouteSettingsViewModel,
    routeType: String,
    destinationLocation: LatLng,
    styleUrl: OSMCustomMapType,
    routeResponse: List<RouteWithLineString>?,
    modifier: Modifier = Modifier
) {
    val coordinates= LatLng(14.165008914904659, 121.24150742562976)
    if (title.isNotEmpty()) {
        OSMMap(
            title = title,
            snippet = snippet,
            routeResponse = routeResponse,
            routeType = routeType,
            routeViewModel = routeViewModel,
            location = coordinates,
            initialLocation = initialLocation,
            destinationLocation = destinationLocation,
            osmMapType = styleUrl
        )
    }
}

