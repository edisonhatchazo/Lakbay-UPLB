package com.edison.lakbayuplb.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edison.lakbayuplb.R
import com.edison.lakbayuplb.ui.navigation.AppViewModelProvider
import com.edison.lakbayuplb.ui.navigation.NavigationDestination
import com.edison.lakbayuplb.ui.settings.global.RouteSettingsViewModel

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
//    var showMapDialog by remember { mutableStateOf(false) }
//    val context = LocalContext.current
//    val uiState by locationViewModel.uiState.collectAsState()
//    val maps = uiState.mapDataDetails
//    val currentLocation by locationViewModel.currentLocation.observeAsState(null)
//    val destinationLocation = GeoPoint(maps.latitude, maps.longitude)
//    val routeResponse by viewModel.routeResponse.observeAsState()
//    var selectedRouteType by remember { mutableStateOf("foot") }
//
//    var styleUrl by remember { mutableStateOf(OSMCustomMapType.STREET) }
//
//    // Check network and location status
//    val isOnline = isOnline(context)
//    val isLocationEnabled = isLocationEnabled(context)
//
//    val coroutineScope = rememberCoroutineScope() // Create a coroutine scope for launching coroutines
//
//
//    val locationPermissionLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission(),
//        onResult = { isGranted: Boolean ->
//            if (isGranted) {
//                locationViewModel.getCurrentLocationOnce()
//            } else {
//                println("Location permission denied")
//            }
//        }
//    )
//
//    DisposableEffect(Unit) {
//        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED) {
//            locationViewModel.getCurrentLocationOnce()
//        } else {
//            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
//        }
//
//        onDispose {
//            locationViewModel.stopLocationUpdates()
//        }
//    }
//
//    var initialLocation by remember {
//        mutableStateOf(
//            when {
//                isOnline && isLocationEnabled -> currentLocation ?: GeoPoint(14.165008914904659, 121.24150742562976) // Default if currentLocation is null
//                isOnline && !isLocationEnabled -> GeoPoint(14.165008914904659, 121.24150742562976) // Set to initial location
//                !isOnline -> null // Set to null if offline
//                else -> GeoPoint(14.165008914904659, 121.24150742562976) // Fallback initial location
//            }
//        )
//    }
//
//    LaunchedEffect(selectedRouteType, initialLocation, destinationLocation) {
//        initialLocation?.let {
//            viewModel.calculateRouteFromUserInput(
//                context,
//                selectedRouteType,
//                it.latitude,
//                it.longitude,
//                destinationLocation.latitude,
//                destinationLocation.longitude,
//                "#OOOOFF"
//            )
//        }
//    }
//
//    Scaffold(
//        modifier = modifier,
//        topBar = {
//            GuideScreenTopAppBar(
//                title = "Guide to Destination",
//                canNavigateBack = true,
//                navigateUp = navigateBack,
//                onRouteTypeSelected = { routeType ->
//                    selectedRouteType = routeType
//                    if (isOnline) {
//                        initialLocation?.let {
//                            viewModel.calculateRouteFromUserInput(
//                                context,
//                                routeType,
//                                it.latitude,
//                                it.longitude,
//                                destinationLocation.latitude,
//                                destinationLocation.longitude,
//                                "#OOOOFF"
//                            )
//                        }
//                    }
//                    if (routeType == "transit") {
//                        viewModel.loadAllBusStops()
//                    }
//                },
//                onChooseLocation = { showMapDialog = true },
//            )
//        }
//    ) { innerPadding ->
//        if (showMapDialog) {
//            GuideMapPointPickerDialog(
//                title = "Choose Starting Point",
//                isOnline = isOnline,
//                isLocationEnabled = isLocationEnabled,
//                startCoordinates = initialLocation,
//                onDismissRequest = { showMapDialog = false },
//                onStartPointSelected = { lat, lng ->
//                    initialLocation = GeoPoint(lat, lng)
//                },
//                onConfirm = {
//                    showMapDialog = false
//                    initialLocation?.let {
//                        viewModel.calculateRouteFromUserInput(
//                            context,
//                            selectedRouteType,
//                            it.latitude,
//                            it.longitude,
//                            destinationLocation.latitude,
//                            destinationLocation.longitude,
//                            "#OOOOFF"
//                        )
//                    }
//                },
//                onGetCurrentLocation = {
//                    currentLocation?.let {
//                        initialLocation = GeoPoint(it.latitude, it.longitude)
//                        viewModel.calculateRouteFromUserInput(
//                            context,
//                            selectedRouteType,
//                            it.latitude,
//                            it.longitude,
//                            destinationLocation.latitude,
//                            destinationLocation.longitude,
//                            "#OOOOFF"
//                        )
//                    }
//                }
//            )
//        }
//
//        GuideMapDetails(
//            initialLocation = initialLocation,
//            destinationLocation = destinationLocation,
//            title = maps.title,
//            routeViewModel = routeViewModel,
//            snippet = maps.snippet,
//            styleUrl = styleUrl,
//            routeResponse = routeResponse,
//            modifier = Modifier
//                .padding(
//                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
//                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
//                    top = innerPadding.calculateTopPadding(),
//                    bottom = innerPadding.calculateBottomPadding()
//                )
//        )
//    }
}
//
//@Composable
//fun GuideMapDetails(
//    initialLocation: GeoPoint?,
//    title: String,
//    snippet: String,
//    routeViewModel: RouteSettingsViewModel,
//    destinationLocation: GeoPoint,
//    styleUrl: OSMCustomMapType,
//    routeResponse: List<RouteWithLineString>?,
//    modifier: Modifier = Modifier
//) {
//    val context = LocalContext.current
//    val coordinates= GeoPoint(14.165008914904659, 121.24150742562976)
//    if (title.isNotEmpty()) {
//
////        OSMMap(
////            title = title,
////            snippet = snippet,
////            routeResponse = routeResponse,
////            routeViewModel = routeViewModel,
////            location = coordinates,
////            initialLocation = initialLocation,
////            destinationLocation = destinationLocation,
////            osmMapType = styleUrl
////        )
//    }
//}

