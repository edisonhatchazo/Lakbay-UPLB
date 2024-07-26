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
import com.example.classschedule.algorithm.osrms.LocationHelper
import com.example.classschedule.algorithm.osrms.RouteResponse
import com.example.classschedule.ui.navigation.AppViewModelProvider
import com.example.classschedule.ui.navigation.NavigationDestination
import com.example.classschedule.ui.screen.GuideScreenTopAppBar
import com.example.classschedule.ui.settings.global.RouteViewModel
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
    routeViewModel: RouteViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val styleUrl = OSMCustomMapType.STREET.styleUrl
    val context = LocalContext.current
    val uiState by locationViewModel.uiState.collectAsState()
    val maps = uiState.mapDataDetails
    var showMapDialog by remember { mutableStateOf(false) }
    val currentLocation by locationViewModel.currentLocation.observeAsState(null)
    val destinationLocation =  LatLng(maps.latitude, maps.longitude)
    val routeResponse by viewModel.routeResponse.observeAsState()
    var selectedRouteType by remember { mutableStateOf("foot") }
    val locationHelper = remember { LocationHelper(context) }

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
    val initialLocation by locationViewModel.currentLocation.observeAsState(LatLng(14.165008914904659, 121.24150742562976)) // Provide a default location if currentLocation is null

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
                title = "Guide to ${maps.title}",
                canNavigateBack = true,
                navigateUp = navigateBack,
                onRouteTypeSelected = { routeType ->
                    currentLocation?.let {
                        viewModel.calculateRouteFromUserInput(
                            routeType,
                            it.latitude,
                            it.longitude,
                            destinationLocation.latitude,
                            destinationLocation.longitude
                        )
                    }
                    if (routeType == "transit") {
                        viewModel.loadAllBusStops()
                    }


                }
            )
        }
    ){ innerPadding ->

        GuideMapDetails(
            initialLocation = initialLocation,
            destinationLocation = destinationLocation,
            title = maps.title,
            routeType = selectedRouteType,
            routeViewModel = routeViewModel,
            snippet = maps.snippet,
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
    initialLocation: LatLng,
    title: String,
    snippet: String,
    routeViewModel: RouteViewModel,
    routeType: String,
    destinationLocation: LatLng,
    routeResponse: List<Pair<RouteResponse, String>>?,
    modifier: Modifier = Modifier
) {
    val styleUrl = OSMCustomMapType.STREET.styleUrl
    if(title!= "") {
        OSMMap(
            title = title,
            snippet = snippet,
            routeResponse = routeResponse,
            routeType = routeType,
            routeViewModel = routeViewModel,
            location = initialLocation,
            destinationLocation = destinationLocation,
            styleUrl = styleUrl
        )
    }
}

