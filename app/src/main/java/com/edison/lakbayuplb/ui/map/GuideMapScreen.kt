package com.edison.lakbayuplb.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
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
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edison.lakbayuplb.R
import com.edison.lakbayuplb.algorithm.notifications.NavigationNotificationService
import com.edison.lakbayuplb.algorithm.routing_algorithm.LocationHelper
import com.edison.lakbayuplb.algorithm.routing_algorithm.RouteWithLineString
import com.edison.lakbayuplb.algorithm.routing_algorithm.extractDirectionAndDistance
import com.edison.lakbayuplb.algorithm.routing_algorithm.getImage
import com.edison.lakbayuplb.algorithm.routing_algorithm.haversineInMeters
import com.edison.lakbayuplb.algorithm.routing_algorithm.isLocationEnabled
import com.edison.lakbayuplb.algorithm.routing_algorithm.parseGeoJSONGeometry
import com.edison.lakbayuplb.ui.navigation.AppViewModelProvider
import com.edison.lakbayuplb.ui.navigation.NavigationDestination
import com.edison.lakbayuplb.ui.screen.GuideScreenTopAppBar
import com.edison.lakbayuplb.ui.settings.global.AppPreferences
import com.edison.lakbayuplb.ui.settings.global.RouteSettingsViewModel
import com.edison.lakbayuplb.ui.settings.global.TopAppBarColorSchemesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
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
    val isLocationEnabled = isLocationEnabled(context)

    val destinationLocation = GeoPoint(maps.latitude,maps.longitude)

    val currentLocation by locationViewModel.currentLocation.observeAsState(null)
    val routeResponse by viewModel.routeResponse.observeAsState()
    var selectedRouteType by rememberSaveable  { mutableStateOf("foot") }
    var isCalculatingRoute = viewModel.isCalculatingRoute
    val isDoubleTransit = routeViewModel.forestryRouteDoubleRideEnabled.collectAsState().value
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                if (isLocationEnabled) {
                    locationViewModel.getCurrentLocationOnce()
                }
            } else {
                println("Location permission denied")
            }
        }
    )

    DisposableEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED) {
            if (isLocationEnabled) {
                locationViewModel.getCurrentLocationOnce()
            }
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        onDispose {
            locationViewModel.stopLocationUpdates()
        }
    }

    var initialLocation by remember {
        mutableStateOf(
            currentLocation?.let { GeoPoint(it.latitude, it.longitude) }
                ?: GeoPoint(14.166995706659199, 121.24302756140631) // Fallback location
        )
    }

    DisposableEffect(currentLocation) {
        currentLocation?.let {
            initialLocation = GeoPoint(it.latitude, it.longitude)
        }
        onDispose { }
    }


    LaunchedEffect(selectedRouteType, initialLocation, destinationLocation) {
        initialLocation.let {
            viewModel.calculateRouteFromUserInput(
                profile = selectedRouteType, // This can be dynamic
                startLat = initialLocation.latitude,
                startLng = initialLocation.longitude,
                endLat =destinationLocation.latitude,
                endLng = destinationLocation.longitude,
                doubleTransit = isDoubleTransit
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
                },
                topAppBarBackgroundColor = topAppBarBackgroundColor,
                topAppBarForegroundColor = topAppBarForegroundColor
            )
        }
    ){ innerPadding ->
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

        LaunchedEffect(key1 = routeResponse, key2 = selectedRouteType) {
            if (routeResponse != null) {
                isCalculatingRoute = false
            }
        }

        val counter = mutableListOf(0)
        GuideMapDetails(
            initialLocation = initialLocation,
            destinationLocation = destinationLocation,
            title = maps.title,
            snippet = maps.snippet,
            counter = counter,
            routeType = selectedRouteType,
            routeResponse = routeResponse,
            modifier = Modifier.padding(0.dp),
            contentPadding = innerPadding,
        )


    }
}

@SuppressLint("DefaultLocale", "MutableCollectionMutableState")
@Composable
fun GuideMapDetails(
    title: String,
    snippet: String,
    initialLocation: GeoPoint?,
    destinationLocation: GeoPoint?,
    counter: MutableList<Int>,
    routeType: String,
    routeResponse: MutableList<Pair<String, MutableList<Pair<String, MutableList<RouteWithLineString>>>>>?,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var userLocation by remember { mutableStateOf(initialLocation) }
    var hasReachedFinalDestination by remember { mutableStateOf(false) }
    val appPreferences = AppPreferences(context)
    val isNavigationEnabled = appPreferences.isNavigationNotificationEnabled()
    val isSpeechEnabled = appPreferences.isNavigationSpeechEnabled()
    var lineString by remember {
        mutableStateOf<MutableList<Pair<String, MutableList<MutableList<Pair<Double, Double>>>>>>(
            mutableListOf() // Initialize with an empty MutableList
        )
    }
    var currentDestinations by remember {
        mutableStateOf<MutableList<MutableList<GeoPoint>>>(mutableListOf())
    }

    if (counter[0] == 0) {
        counter[0]++
        lineString = routeResponse?.mapNotNull { profilePair ->
            val profile = profilePair.first // Profile type, e.g., "foot", "bicycle", etc.

            val routes = profilePair.second.flatMap { segmentPair ->
                // Extract and parse each RouteWithLineString
                segmentPair.second.mapNotNull { route ->

                    route.lineString.let { geoJson ->
                        parseGeoJSONGeometry(geoJson).toMutableList()
                    }
                }
            }.toMutableList() // Combine all routes into a MutableList
            if (routes.isNotEmpty()) profile to routes else null
        }?.toMutableList() ?: mutableListOf()
        currentDestinations = getDestinations(lineString)
    }
    // Instructions and related UI variables
    var currentProfile by remember { mutableStateOf("foot") }
    var remainingDistance by remember { mutableIntStateOf(0) }
    var turningDistance by remember { mutableIntStateOf(0) }
    var currentInstruction by remember { mutableStateOf("No instructions available.") }
    var instructionList by remember { mutableStateOf(listOf<String>()) }
    var direction by remember { mutableStateOf("Continue straight") }
    var destinationString by remember { mutableStateOf("Destination")}
    var currentImage by remember { mutableIntStateOf(R.drawable.straight) }
    var mode by remember { mutableIntStateOf(R.drawable.walking_icon) }
    val tts = remember {
        TextToSpeech(context) { status ->
            if (status != TextToSpeech.SUCCESS) {
                Toast.makeText(context, "TTS Initialization failed", Toast.LENGTH_SHORT).show()
            }
        }
    }



    DisposableEffect(Unit) {
        val locationHelper = LocationHelper(context)
        val lifecycle = lifecycleOwner.lifecycle

        val serviceIntent = Intent(context, NavigationNotificationService::class.java).apply {
            putExtra("title", title)
            putExtra("currentInstruction", currentInstruction)
        }

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    val updateIntent = Intent("UPDATE_NAVIGATION_INSTRUCTION").apply {
                        putExtra("currentInstruction", currentInstruction)
                    }
                    context.sendBroadcast(updateIntent)
                }
                Lifecycle.Event.ON_DESTROY -> {
                    serviceIntent.action = "STOP_SERVICE"
                    context.startService(serviceIntent)
                    locationHelper.stopLocationUpdates()
                }
                else -> {

                }
            }
        }
        if(isNavigationEnabled) {
            context.startForegroundService(serviceIntent)
        }

        lifecycle.addObserver(observer)

        locationHelper.startLocationUpdates(
            onLocationUpdate = { location ->
                userLocation = GeoPoint(location.latitude, location.longitude)
            },
            onFailure = { exception ->
                Log.e("Location", "Error fetching location: ${exception.message}")
            }
        )

        onDispose {
            lifecycle.removeObserver(observer)
            locationHelper.stopLocationUpdates()
            tts.stop()
            tts.shutdown()
            if(isNavigationEnabled) {
                val stopIntent = Intent(context, NavigationNotificationService::class.java).apply {
                    action = "STOP_SERVICE"
                }
                context.startService(stopIntent)
            }
        }
    }



    LaunchedEffect(userLocation) {
        // Continuously update based on user location changes
        if (lineString.isNotEmpty() && userLocation != null) {
            currentProfile = lineString.first().first
            val route = lineString[0].second.first()
            destinationString = when {
                (currentProfile == "driving" || currentProfile == "bicycle") && !routeResponse.isNullOrEmpty() && routeResponse.size > 1 -> "Parking"
                currentProfile == "foot" && !routeResponse.isNullOrEmpty() && routeResponse.size > 1 -> "Jeepney Stop"
                currentProfile == "transit" -> "Drop Off"
                else -> "Destination"
            }
            mode = when (currentProfile) {
                "foot" -> R.drawable.walking_icon  //Walking
                "bicycle" -> R.drawable.cycling_icon  // Blue for cycling
                "driving" -> R.drawable.car_icon  // Red for driving
                else -> R.mipmap.transit  // Black for any other type
            }
            // Calculate remaining distance and update instructions
            remainingDistance = calculateTotalDistance(route, userLocation?: GeoPoint(14.167064701338353, 121.24304070096179)).toInt()
            instructionList = generateInstructionsFromLineString(route)
            if (instructionList.isNotEmpty()) instructionList = instructionList.drop(1).toMutableList()
            currentInstruction = instructionList.firstOrNull() ?: "No instructions available."

            val (newDirection, newTurningDistance) = extractDirectionAndDistance(currentInstruction)
            direction = newDirection
            turningDistance = newTurningDistance
            currentImage = getImage(direction)
            // Check for deviations and update route if needed
            val nextPoint = route.firstOrNull()
            if (nextPoint != null) {
                val distanceToNextPoint = haversineInMeters(
                    userLocation!!.latitude,
                    userLocation!!.longitude,
                    nextPoint.first,
                    nextPoint.second
                )

                turningDistance += distanceToNextPoint.toInt()
                remainingDistance += distanceToNextPoint.toInt()
                currentInstruction = "$direction in $turningDistance meters."
                if(isNavigationEnabled) {
                    val updateServiceIntent =
                        Intent(context, NavigationNotificationService::class.java).apply {
                            putExtra("title", title)
                            putExtra("currentInstruction", currentInstruction)
                        }
                    context.startForegroundService(updateServiceIntent)
                }

                val deviationThreshold = when (currentProfile) {
                    "foot" -> 2.0
                    "bicycle" -> 3.0
                    "driving" -> 5.0
                    "transit" -> 4.0
                    else -> 2.0
                }

                // Remove point if user is close enough
                if (distanceToNextPoint < deviationThreshold) {
                    route.removeAt(0)
                }

                // Handle user deviation
                if (distanceToNextPoint > deviationThreshold * 3) {
                    // Trigger coroutine for recalculating route
                    withContext(Dispatchers.IO) {
                        val newRoute = getNewRoute(
                            context = context,
                            currentProfile = currentProfile,
                            userLocation = userLocation!!,
                            destinations = currentDestinations.first(),
                        )
                        lineString[0].second[0] = newRoute
                    }
                }
            }

            val finalDestinations = currentDestinations.firstOrNull()
            if (finalDestinations != null) {
                val isNearAnyDestination = finalDestinations.any { destination ->
                    val distanceToDestination = haversineInMeters(
                        userLocation!!.latitude,
                        userLocation!!.longitude,
                        destination.latitude,
                        destination.longitude
                    )
                    distanceToDestination <= 10.0
                }

                if (isNearAnyDestination) {
                    if (lineString.size > 1) {
                        // Remove current segment and proceed to the next
                        lineString.removeAt(0)
                        currentDestinations.removeAt(0)
                    } else {
                        // Final destination reached
                        hasReachedFinalDestination = true
                        currentInstruction = "You have reached your destination."
                        instructionList = listOf(currentInstruction)
                    }
                }
            }
        }

        // Update frequency based on profile
        val updateFrequency = when (currentProfile) {
            "foot" -> 1000L
            "bicycle" -> 1500L
            "driving" -> 3000L
            else -> 2000L
        }
        delay(updateFrequency)
    }

    LaunchedEffect(currentInstruction) {
        // Periodically perform TTS
        if(isSpeechEnabled) {
            while (true) {
                delay(10000L) // 10 seconds
                if (currentInstruction.isNotBlank()) {
                    tts.speak(currentInstruction, TextToSpeech.QUEUE_FLUSH, null, null)
                }
            }
        }
    }


    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT


    val weight = .15f

    Box(modifier = Modifier.fillMaxWidth()){
        OSMMapView(modifier,title,snippet,userLocation,routeType,lineString,destinationLocation)
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
                    Text(text = "${remainingDistance}m until \n$destinationString", color = Color.White, fontSize = 14.sp)

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
                Text(text = "${remainingDistance}m until \n$destinationString", color = Color.White, fontSize = 14.sp)
            }
        }
    }


}
