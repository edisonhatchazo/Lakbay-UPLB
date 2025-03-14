package com.edison.lakbayuplb.ui.map

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edison.lakbayuplb.algorithm.routing_algorithm.RouteWithLineString
import com.edison.lakbayuplb.ui.navigation.AppViewModelProvider
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
//    val topAppBarColors = colorViewModel.topAppBarColors.collectAsState()
//    val (topAppBarBackgroundColor, topAppBarForegroundColor) = topAppBarColors.value
//
//    var showMapDialog by remember { mutableStateOf(false) }
//    var initialLocation by rememberSaveable(stateSaver = geoPointSaver()) { mutableStateOf(null) }
//    var destinationLocation by rememberSaveable(stateSaver = geoPointSaver()) { mutableStateOf(null) }
//
//    val routeResponse by viewModel.routeResponse.observeAsState()
//    var selectedRouteType by rememberSaveable  { mutableStateOf("foot") }
//    var isCalculatingRoute = viewModel.isCalculatingRoute
//    val isDoubleTransit = routeViewModel.forestryRouteDoubleRideEnabled.collectAsState().value
//
//    Scaffold(
//        modifier = modifier,
//        topBar = {
//            MapScreenTopAppBar(
//                title = stringResource(R.string.map),
//                onGetDirectionsClick = { showMapDialog = true },
//                openDrawer = openDrawer,
//                onRouteTypeSelected = { routeType ->
//                    selectedRouteType = routeType
//                    if (initialLocation != null && destinationLocation != null) {
//                        viewModel.calculateRouteFromUserInput(
//                            profile = selectedRouteType, // This can be dynamic
//                            startLat = initialLocation!!.latitude,
//                            startLng = initialLocation!!.longitude,
//                            endLat =destinationLocation!!.latitude,
//                            endLng = destinationLocation!!.longitude,
//                            doubleTransit = isDoubleTransit,
//                            colorCode = when (selectedRouteType) {
//                            "foot" -> "#00FF00"  // Green for foot
//                            "bicycle" -> "#0000FF"  // Blue for cycling
//                            "driving" -> "#FF0000"  // Red for driving
//                            else -> "#000000"  // Black for any other type
//                            }
//                        )
//                    }
//                },
//                topAppBarBackgroundColor = topAppBarBackgroundColor,
//                topAppBarForegroundColor = topAppBarForegroundColor
//            )
//        }
//    ){ innerPadding ->
//        if (showMapDialog) {
//            MapPointPickerDialog(
//                title = "Get Directions",
//                startCoordinates = initialLocation,
//                destinationCoordinates = destinationLocation,
//                onDismissRequest = { showMapDialog = false },
//                onStartPointSelected = { lat, lng ->
//                    initialLocation = GeoPoint(lat, lng)
//                },
//                onDestinationPointSelected = { lat, lng ->
//                    destinationLocation = GeoPoint(lat, lng)
//                },
//                onConfirm = {
//                    // Reset the map state and trigger route calculation
//                    showMapDialog = false
//                    viewModel.calculateRouteFromUserInput(
//                        profile = selectedRouteType, // This can be dynamic
//                        startLat = initialLocation!!.latitude,
//                        startLng = initialLocation!!.longitude,
//                        endLat =destinationLocation!!.latitude,
//                        endLng = destinationLocation!!.longitude,
//                        doubleTransit = isDoubleTransit,
//                        colorCode = when (selectedRouteType) {
//                            "foot" -> "#00FF00"  // Green for foot
//                            "bicycle" -> "#0000FF"  // Blue for cycling
//                            "driving" -> "#FF0000"  // Red for driving
//                            else -> "#000000"  // Black for any other type
//                        },
//                    )
//                }
//
//            )
//        }
//
//        if (isCalculatingRoute) {
//            AlertDialog(
//                onDismissRequest = {},
//                title = {Text("Calculating Route")},
//                text = {
//                    Text(
//                        if (isDoubleTransit) "Calculating Double Transit Route..."
//                        else "Calculating the Transit Route..."
//                    )
//                },
//                confirmButton = {}
//            )
//        }
//
//        LaunchedEffect(key1 = routeResponse, selectedRouteType) {
//            if (routeResponse != null) {
//                isCalculatingRoute = false
//            }
//        }
//
//
//        MapDetails(
//            initialLocation = initialLocation,
//            routeType = selectedRouteType,
//            destinationLocation = destinationLocation,
//            routeResponse = routeResponse,
//            routeViewModel = routeViewModel,
//            modifier = Modifier.padding(0.dp),
//            contentPadding = innerPadding,
//        )
//
//
//    }
}

@Composable
fun MapDetails(
    initialLocation: GeoPoint?,
    destinationLocation: GeoPoint?,
    routeViewModel: RouteSettingsViewModel,
    routeType: String,
    routeResponse: MutableList<Pair<String, MutableList<Pair<String, MutableList<RouteWithLineString>>>>>?,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
//
//    var instructionList = mutableListOf<String>()
//    var currentInstruction: String
//    var remainingDistance = routeResponse?.getOrNull(0)?.second
//        ?.sumOf { pair ->
//            pair.second.sumOf { routeWithLineString ->
//                routeWithLineString.route.legs.sumOf { leg ->
//                    leg.distance
//                }
//            }
//        } ?: 0.0
//    var turningDistance = 0
//    var direction: String
//    var currentProfile = routeResponse?.getOrNull(0)?.first
//    var currentImage = R.drawable.left
//    remainingDistance = String.format("%.2f", remainingDistance).toDouble()
//
//    var destination = if((currentProfile == "driving" || currentProfile == "bicycle") && routeResponse?.size!! >1) "Parking"
//    else if(currentProfile == "foot" && routeResponse?.size!! > 1) "Jeepney Stop"
//    else if(currentProfile == "transit") "Drop Off" else "Destination"
//
//    currentInstruction = "No instructions available."
////    routeResponse?.forEach { routePair ->
////        routePair.second.forEach { innerPair ->
////            innerPair.second.firstOrNull()?.let { routeWithLineString ->
////                val routeInstructions = generateRouteInstructions(routeWithLineString)
////                instructionList.addAll(routeInstructions)
////            }
////        }
////    }
////
////    if (instructionList.isNotEmpty()) {
////        currentInstruction = instructionList[0] // Safely access the first element
////        val (extractedDirection,extractedTurningDistance) = extractDirectionAndDistance(currentInstruction)
////
////        direction = extractedDirection
////        turningDistance = extractedTurningDistance
////        currentImage = getImage(direction)
////    } else {
////        // Handle the case where the list is empty
////        currentInstruction = "No instructions available."
////    }
//
//    val configuration = LocalConfiguration.current
//    val isPortrait = configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT
//
//    var mode = when (currentProfile) {
//        "foot" -> R.drawable.walking_icon  //Walking
//        "bicycle" -> R.drawable.cycling_icon  // Blue for cycling
//        "driving" -> R.drawable.car_icon  // Red for driving
//        else -> R.mipmap.transit  // Black for any other type
//    }
//    val weight = .15f
//
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//
//    ){
//        val title = "UPLB"
//        val snippet = "2nd Floor"
//        //OSMMapView(modifier,title,snippet,initialLocation,routeType,routeResponse,destinationLocation)
//    }
//    if(isPortrait) {
//        Column(modifier = modifier.padding(16.dp)) {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(contentPadding)
//                    .background(Color.Green)
//                    .border(BorderStroke(4.dp, Color.Gray)),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Column(
//                    modifier = Modifier
//                        .background(Color.Blue)
//                        .padding(start = 8.dp, end = 8.dp, top = 0.dp, bottom = 0.dp)
//                        .fillMaxHeight(weight),
//
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    verticalArrangement = Arrangement.Center
//                ) {
//                    Image(
//                        painter = painterResource(id = currentImage),
//                        contentDescription = "Directions",
//                        modifier = Modifier.size(48.dp)
//                    )
//                    Text(text = "${turningDistance}m", color = Color.White, fontSize = 16.sp)
//                }
//                // Instructions
//                Column(
//                    modifier = Modifier
//                        .padding(start = 0.dp, end = 0.dp, top = 0.dp, bottom = 0.dp)
//                        .background(Color.Green)
//                        .fillMaxHeight(weight)
//                        .fillMaxWidth(.5f),
//
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    verticalArrangement = Arrangement.Center
//                ) {
//                    Text(
//                        text = currentInstruction,
//                        color = Color.Black,
//                        fontSize = 16.sp
//                    )
//                }
//
//                // Right section with walking icon and distance
//                Column(
//                    modifier = Modifier
//                        .background(Color.Blue)
//                        .padding(start = 8.dp, end = 8.dp, top = 0.dp, bottom = 0.dp)
//                        .fillMaxHeight(weight),
//
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    verticalArrangement = Arrangement.Center
//                ) {
//                    Image(
//                        painter = painterResource(id = mode), // Mode of Transport
//                        contentDescription = "Mode of Transport",
//                        modifier = Modifier.size(48.dp),
//                        colorFilter = ColorFilter.tint(Color.White)
//                    )
//                    Text(text = "${remainingDistance}m until \n$destination", color = Color.White, fontSize = 14.sp)
//
//                }
//            }
//        }
//    }else{
//        Row(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(top = 128.dp, start = 16.dp, bottom = 16.dp, end = 64.dp),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Column(
//                modifier = Modifier
//                    .background(Color.Blue)
//                    .padding(start = 8.dp, end = 8.dp, top = 0.dp, bottom = 0.dp)
//                    .fillMaxHeight(.75f)
//                    .fillMaxWidth(.25f),
//
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Center
//            ) {
//                Image(
//                    painter = painterResource(id = currentImage),
//                    contentDescription = "Directions",
//                    modifier = Modifier.size(48.dp)
//                )
//                Text(text = "${turningDistance}m", color = Color.White, fontSize = 16.sp)
//                Spacer(modifier = Modifier.height(25.dp))
//                Text(
//                    text = currentInstruction,
//                    color = Color.White,
//                    fontSize = 16.sp
//                )
//            }
//
//            // Map in the center
//            Box(
//                modifier = Modifier
//                    .weight(2f)
//                    .fillMaxHeight()
//            ) {
//
//            }
//
//            // Right section (extra info, destination details)
//            Column(
//                modifier = Modifier
//                    .background(Color.Blue)
//                    .padding(start = 8.dp, end = 8.dp, top = 0.dp, bottom = 0.dp)
//                    .fillMaxHeight(.5f),
//
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Center
//            ) {
//                Image(
//                    painter = painterResource(id = mode), // Mode of Transport
//                    contentDescription = "Mode of Transport",
//                    modifier = Modifier.size(48.dp),
//                    colorFilter = ColorFilter.tint(Color.White)
//                )
//                Text(text = "${remainingDistance}m until \n $destination", color = Color.White, fontSize = 14.sp)
//            }
//        }
//    }


}
