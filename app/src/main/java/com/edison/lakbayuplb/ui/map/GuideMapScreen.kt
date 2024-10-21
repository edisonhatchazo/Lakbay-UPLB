package com.edison.lakbayuplb.ui.map

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edison.lakbayuplb.R
import com.edison.lakbayuplb.algorithm.routing_algorithm.RouteWithLineString
import com.edison.lakbayuplb.algorithm.routing_algorithm.extractDirectionAndDistance
import com.edison.lakbayuplb.algorithm.routing_algorithm.generateRouteInstructions
import com.edison.lakbayuplb.algorithm.routing_algorithm.getImage
import com.edison.lakbayuplb.algorithm.routing_algorithm.isLocationEnabled
import com.edison.lakbayuplb.algorithm.routing_algorithm.isOnline
import com.edison.lakbayuplb.ui.navigation.AppViewModelProvider
import com.edison.lakbayuplb.ui.navigation.NavigationDestination
import com.edison.lakbayuplb.ui.screen.GuideScreenTopAppBar
import com.edison.lakbayuplb.ui.settings.global.RouteSettingsViewModel
import com.edison.lakbayuplb.ui.settings.global.TopAppBarColorSchemesViewModel
import org.osmdroid.util.GeoPoint

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
    routeViewModel: RouteSettingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    colorViewModel: TopAppBarColorSchemesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val topAppBarColors = colorViewModel.topAppBarColors.collectAsState()
    val (topAppBarBackgroundColor, topAppBarForegroundColor) = topAppBarColors.value
    val uiState by locationViewModel.uiState.collectAsState()
    val maps = uiState.mapDataDetails
    val context = LocalContext.current
    val isOnline = isOnline(context)
    val isLocationEnabled = isLocationEnabled(context)

    var showMapDialog by remember { mutableStateOf(false) }
    var destinationLocation = GeoPoint(maps.latitude,maps.longitude)

    val currentLocation by locationViewModel.currentLocation.observeAsState(null)
    val routeResponse by viewModel.routeResponse.observeAsState()
    var selectedRouteType by rememberSaveable  { mutableStateOf("foot") }
    var isCalculatingRoute = viewModel.isCalculatingRoute
    val isDoubleTransit = routeViewModel.forestryRouteDoubleRideEnabled.collectAsState().value
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

    var initialLocation by remember {
        mutableStateOf<GeoPoint?>(
            GeoPoint(14.165008914904659, 121.24150742562976) // Fallback initial location
         )
    }

    LaunchedEffect(selectedRouteType, initialLocation, destinationLocation) {
        initialLocation?.let {
            viewModel.calculateRouteFromUserInput(
                profile = selectedRouteType, // This can be dynamic
                startLat = initialLocation!!.latitude,
                startLng = initialLocation!!.longitude,
                endLat =destinationLocation.latitude,
                endLng = destinationLocation.longitude,
                doubleTransit = isDoubleTransit,
                colorCode = when (selectedRouteType) {
                    "foot" -> "#00FF00"  // Green for foot
                    "bicycle" -> "#0000FF"  // Blue for cycling
                    "driving" -> "#FF0000"  // Red for driving
                    else -> "#000000"  // Black for any other type
                }
            )
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            GuideScreenTopAppBar(
                title = stringResource(R.string.guide),
                canNavigateBack = true,
                navigateUp = navigateBack,
                onRouteTypeSelected = { routeType ->
                    selectedRouteType = routeType
                    if (initialLocation != null) {
                        viewModel.calculateRouteFromUserInput(
                            profile = selectedRouteType, // This can be dynamic
                            startLat = initialLocation!!.latitude,
                            startLng = initialLocation!!.longitude,
                            endLat =destinationLocation.latitude,
                            endLng = destinationLocation.longitude,
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
                topAppBarForegroundColor = topAppBarForegroundColor,
                onChooseLocation = { showMapDialog = true },
            )
        }
    ){ innerPadding ->
        if (showMapDialog) {
            GuideMapPointPickerDialog(
                title = "Choose Starting Point",
                isOnline = isOnline,
                isLocationEnabled = isLocationEnabled,
                startCoordinates = GeoPoint(initialLocation),
                onDismissRequest = { showMapDialog = false },
                onStartPointSelected = { lat, lng ->
                    initialLocation = GeoPoint(lat, lng)
                },
                onConfirm = {
                    showMapDialog = false
                    initialLocation?.let {
                        viewModel.calculateRouteFromUserInput(
                            profile = selectedRouteType, // This can be dynamic
                            startLat = initialLocation!!.latitude,
                            startLng = initialLocation!!.longitude,
                            endLat =destinationLocation.latitude,
                            endLng = destinationLocation.longitude,
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
                onGetCurrentLocation = {
                    currentLocation?.let {
                        initialLocation = GeoPoint(it.latitude, it.longitude)
                        viewModel.calculateRouteFromUserInput(
                            profile = selectedRouteType, // This can be dynamic
                            startLat = initialLocation!!.latitude,
                            startLng = initialLocation!!.longitude,
                            endLat =destinationLocation.latitude,
                            endLng = destinationLocation.longitude,
                            doubleTransit = isDoubleTransit,
                            colorCode = when (selectedRouteType) {
                                "foot" -> "#00FF00"  // Green for foot
                                "bicycle" -> "#0000FF"  // Blue for cycling
                                "driving" -> "#FF0000"  // Red for driving
                                else -> "#000000"  // Black for any other type
                            }
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


        GuideMapDetails(
            initialLocation = initialLocation,
            destinationLocation = destinationLocation,
            routeViewModel = routeViewModel,
            title = maps.title,
            snippet = maps.snippet,
            routeType = selectedRouteType,
            routeResponse = routeResponse,
            modifier = Modifier.padding(0.dp),
            contentPadding = innerPadding,
        )


    }
}


@Composable
fun GuideMapDetails(
    title: String,
    snippet: String,
    initialLocation: GeoPoint?,
    destinationLocation: GeoPoint?,
    routeViewModel: RouteSettingsViewModel,
    routeType: String,
    routeResponse: List<RouteWithLineString>?,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {

    var instructionList = mutableListOf<String>()
    var currentInstruction: String
    var remainingDistance = 0
    var turningDistance = 0
    var direction: String
    var currentProfile = "foot"
    var currentImage = R.drawable.left
    routeResponse?.forEach{routeWithLineString ->
        val routeInstructions = generateRouteInstructions(routeWithLineString)
        instructionList.addAll(routeInstructions)
    }

    if (instructionList.isNotEmpty()) {
        currentInstruction = instructionList[0] // Safely access the first element
        val (extractedDirection,extractedTurningDistance) = extractDirectionAndDistance(currentInstruction)

        direction = extractedDirection
        turningDistance = extractedTurningDistance
        currentImage = getImage(direction)
    } else {
        // Handle the case where the list is empty
        currentInstruction = "No instructions available."
    }

    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT

    var mode = when (routeType) {
        "foot" -> R.drawable.walking_icon  //Walking
        "bicycle" -> R.drawable.cycling_icon  // Blue for cycling
        "driving" -> R.drawable.car_icon  // Red for driving
        else -> R.mipmap.transit  // Black for any other type
    }
    val weight = .15f
    Box(
        modifier = Modifier
            .fillMaxWidth()

    ){
        OSMMapView(modifier,title,snippet,initialLocation,routeType,routeResponse,destinationLocation)
    }
    if(isPortrait) {
        Column(
            modifier = modifier
                .padding(16.dp)

        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding)
                    .background(Color.Green)
                    .border(BorderStroke(4.dp, Color.Gray)),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .background(Color.Blue)
                        .padding(start = 8.dp, end = 8.dp, top = 0.dp, bottom = 0.dp)
                        .fillMaxHeight(weight),

                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = currentImage),
                        contentDescription = "Directions",
                        modifier = Modifier.size(48.dp)
                    )
                    Text(text = "${turningDistance}m", color = Color.White, fontSize = 16.sp)
                }
                // Instructions
                Column(
                    modifier = Modifier
                        .padding(start = 0.dp, end = 0.dp, top = 0.dp, bottom = 0.dp)
                        .background(Color.Green)
                        .fillMaxHeight(weight)
                        .fillMaxWidth(.5f),

                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = currentInstruction,
                        color = Color.Black,
                        fontSize = 16.sp
                    )
                }

                // Right section with walking icon and distance
                Column(
                    modifier = Modifier
                        .background(Color.Blue)
                        .padding(start = 8.dp, end = 8.dp, top = 0.dp, bottom = 0.dp)
                        .fillMaxHeight(weight),

                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = mode), // Mode of Transport
                        contentDescription = "Mode of Transport",
                        modifier = Modifier.size(48.dp),
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                    Text(text = "${remainingDistance}m until \nDestination", color = Color.White, fontSize = 14.sp)

                }
            }
        }
    }else{
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 128.dp, start = 16.dp, bottom = 16.dp, end = 64.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .background(Color.Blue)
                    .padding(start = 8.dp, end = 8.dp, top = 0.dp, bottom = 0.dp)
                    .fillMaxHeight(.75f)
                    .fillMaxWidth(.25f),

                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = currentImage),
                    contentDescription = "Directions",
                    modifier = Modifier.size(48.dp)
                )
                Text(text = "${turningDistance}m", color = Color.White, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(25.dp))
                Text(
                    text = currentInstruction,
                    color = Color.White,
                    fontSize = 16.sp
                )
            }

            // Map in the center
            Box(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight()
            ) {

            }

            // Right section (extra info, destination details)
            Column(
                modifier = Modifier
                    .background(Color.Blue)
                    .padding(start = 8.dp, end = 8.dp, top = 0.dp, bottom = 0.dp)
                    .fillMaxHeight(.5f),

                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = mode), // Mode of Transport
                    contentDescription = "Mode of Transport",
                    modifier = Modifier.size(48.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
                Text(text = "${remainingDistance}m until \nDestination", color = Color.White, fontSize = 14.sp)
            }
        }
    }


}

