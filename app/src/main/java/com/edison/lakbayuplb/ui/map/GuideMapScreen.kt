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
import androidx.compose.runtime.mutableDoubleStateOf
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
import com.edison.lakbayuplb.algorithm.routing_algorithm.getImage
import com.edison.lakbayuplb.algorithm.routing_algorithm.haversineInMeters
import com.edison.lakbayuplb.algorithm.routing_algorithm.isLocationEnabled
import com.edison.lakbayuplb.algorithm.routing_algorithm.parseGeoJSONGeometry
import com.edison.lakbayuplb.ui.navigation.AppViewModelProvider
import com.edison.lakbayuplb.ui.navigation.NavigationDestination
import com.edison.lakbayuplb.ui.screen.GuideScreenTopAppBar
import com.edison.lakbayuplb.ui.settings.global.AppPreferences
import com.edison.lakbayuplb.ui.settings.global.RouteSettingsViewModel
import com.edison.lakbayuplb.ui.settings.global.SpeedPreferences
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
    var isCalculatingRoute by remember {mutableStateOf(true)}
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
        isCalculatingRoute = true

        val calculationResult = initialLocation.let {
            viewModel.calculateRouteFromUserInput(
                profile = selectedRouteType,
                startLat = initialLocation.latitude,
                startLng = initialLocation.longitude,
                endLat = destinationLocation.latitude,
                endLng = destinationLocation.longitude,
                doubleTransit = isDoubleTransit
            )
        }
        isCalculatingRoute = !calculationResult // Set to false if calculation is complete
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
                    isCalculatingRoute = true
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
            navigateBack = navigateBack,
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
    navigateBack: () -> Unit,
    title: String,
    snippet: String,
    initialLocation: GeoPoint?,
    destinationLocation: GeoPoint?,
    counter: MutableList<Int>,
    routeType: String,
    routeResponse: MutableList<Pair<String, MutableList<Pair<String, MutableList<RouteWithLineString>>>>>?,
    modifier: Modifier = Modifier,
    mapViewModel: MapViewModel = viewModel(factory = AppViewModelProvider.Factory),
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val walkingSpeed = SpeedPreferences(context).walkingSpeed
    val cyclingSpeed = SpeedPreferences(context).cyclingSpeed/3.6
    val drivingSpeed = 5.56
    val transitSpeed = 2.77
    var userLocation by remember { mutableStateOf(initialLocation) }
    val appPreferences = AppPreferences(context)
    val isNavigationEnabled = appPreferences.isNavigationNotificationEnabled()
    val isSpeechEnabled = appPreferences.isNavigationSpeechEnabled()
    var currentProfilePoints by remember{mutableStateOf<MutableList<MutableList<GeoPoint>>>(mutableListOf())}
    var currentPoints by remember{mutableStateOf<MutableList<GeoPoint>>(mutableListOf())}
    var currentRoute by remember{mutableStateOf<MutableList<GeoPoint>>(mutableListOf())}
    var currentPointDistance by remember{ mutableIntStateOf(0)}
    var currentRouteString by remember{ mutableStateOf("")}
    var otherRoute by remember{mutableStateOf<MutableList<GeoPoint>>(mutableListOf())}
    var otherRouteString by remember{ mutableStateOf("")}
    var lineString by remember {
        mutableStateOf<MutableList<Pair<String, MutableList<MutableList<Pair<Double, Double>>>>>>(
            mutableListOf()
        )
    }
    var currentDistances by remember{mutableStateOf<MutableList<Int>>(mutableListOf())}
    var totalDistances by remember{ mutableIntStateOf(0)}
    var turningPoints by remember {
        mutableStateOf<MutableList<Pair<String, MutableList<Pair<String, GeoPoint>>>>>(mutableListOf())
    }
    var currentDestinations by remember {
        mutableStateOf<MutableList<MutableList<GeoPoint>>>(mutableListOf())
    }
    var currentProfile by remember { mutableStateOf("foot") }
    var remainingDistance by remember { mutableIntStateOf(0) }
    var turningDistance by remember { mutableIntStateOf(0) }
    var currentInstruction by remember { mutableStateOf("No instructions available.") }
    var direction by remember { mutableStateOf("Continue straight") }
    val directions by remember{mutableStateOf<MutableList<String>>(mutableListOf())}
    var destinationString by remember { mutableStateOf("Destination")}
    var currentImage by remember { mutableIntStateOf(R.drawable.straight) }
    var mode by remember { mutableIntStateOf(R.drawable.walking_icon) }
    var currentDistance by remember {mutableDoubleStateOf(0.0)}
    var duration by remember{mutableIntStateOf(0)}
    var previousPointDistance by remember { mutableIntStateOf(0) }
    var deviation by remember { mutableIntStateOf(0)}


    fun processTransitRoutes(routeResponse: MutableList<Pair<String, MutableList<Pair<String, MutableList<RouteWithLineString>>>>>?) {
        var forestryRoute: MutableList<Pair<Double, Double>>? = null
        var kananRoute: MutableList<Pair<Double, Double>>? = null
        var kaliwaRoute: MutableList<Pair<Double, Double>>? = null

        routeResponse?.forEach { profilePair ->
            val routeTypes = profilePair.first // e.g., "Transit" or "Foot"
            if (routeTypes == "transit") {
                profilePair.second.forEach { routePair ->
                    val routeName = routePair.first // e.g., "Forestry Route Up", "Kaliwa Route 1"
                    val routeSegments = routePair.second // List of RouteWithLineString

                    // Extract the first route segment (most optimal)
                    val lineStrings = routeSegments.firstOrNull()?.lineString?.let { geoJson ->
                        parseGeoJSONGeometry(geoJson).toMutableList()
                    }

                    when {
                        routeName.contains("Forestry", ignoreCase = true) -> forestryRoute = lineStrings
                        routeName.contains("Kaliwa", ignoreCase = true) -> kaliwaRoute = lineStrings
                        routeName.contains("Kanan", ignoreCase = true) -> kananRoute = lineStrings
                    }
                }
            }
        }

        // Determine current and other routes
        when {
            forestryRoute != null -> {
                currentRoute = forestryRoute!!.map { GeoPoint(it.first, it.second) }.toMutableList()
                currentRouteString = "Forestry"

                if (kaliwaRoute != null) {
                    otherRoute = kaliwaRoute!!.map { GeoPoint(it.first, it.second) }.toMutableList()
                    otherRouteString = "Kaliwa"
                } else if (kananRoute != null) {
                    otherRoute = kananRoute!!.map { GeoPoint(it.first, it.second) }.toMutableList()
                    otherRouteString = "Kanan"
                }
            }
            kaliwaRoute != null -> {
                currentRoute = kaliwaRoute!!.map { GeoPoint(it.first, it.second) }.toMutableList()
                currentRouteString = "Kaliwa"

                if (kananRoute != null) {
                    otherRoute = kananRoute!!.map { GeoPoint(it.first, it.second) }.toMutableList()
                    otherRouteString = "Kanan"
                }
            }
            kananRoute != null -> {
                currentRoute = kananRoute!!.map { GeoPoint(it.first, it.second) }.toMutableList()
                currentRouteString = "Kanan"

                if (kaliwaRoute != null) {
                    otherRoute = kaliwaRoute!!.map { GeoPoint(it.first, it.second) }.toMutableList()
                    otherRouteString = "Kaliwa"
                }
            }
        }
    }
    fun extractTurningDirections() {
        if (turningPoints.isNotEmpty()) {
            val turningPointDirections = turningPoints[0].second.map { it.first }
            directions.clear()
            directions.addAll(turningPointDirections)
        }
    }


    if (counter[0] == 0) {
        counter[0]++
        processTransitRoutes(routeResponse)
        lineString = routeResponse?.mapNotNull { profilePair ->
            val profile = profilePair.first // Profile type, e.g., "foot", "bicycle", etc.

            val segments = profilePair.second.flatMap { routePair ->
                routePair.second.map { routeWithLineString ->
                    routeWithLineString.lineString.let { geoJson ->
                        parseGeoJSONGeometry(geoJson).toMutableList()
                    }
                }
            }

            if (segments.isNotEmpty()) profile to segments.toMutableList() else null
        }?.toMutableList() ?: mutableListOf()

        for (i in 0 until lineString.size - 1) {
            val currentProfileRoutes = lineString[i].second
            val nextProfileRoutes = lineString[i + 1].second

            // Get the last point of the current profile's first route
            val lastPoint = currentProfileRoutes.firstOrNull()?.lastOrNull()
            if (lastPoint != null) {
                // Add the last point to the start of the next profile's first route
                nextProfileRoutes.firstOrNull()?.add(0, lastPoint)
            }
        }

        // Add the final destination location to the last profile's last route
        destinationLocation?.let { destination ->
            val lastProfileRoutes = lineString.lastOrNull()?.second
            lastProfileRoutes?.lastOrNull()?.add(Pair(destination.latitude, destination.longitude))
        }
        if(lineString.isNotEmpty()) {
            currentProfile = lineString.first().first
            currentDestinations = getDestinations(lineString)

            turningPoints = reduceToDirections(lineString)
            currentProfilePoints = getCurrentPoints(lineString, turningPoints)
            if (currentProfilePoints.isNotEmpty()) {
                currentPoints = currentProfilePoints[0]
                currentPoints.add(
                    0,
                    userLocation ?: GeoPoint(14.166995706659199, 121.24302756140631)
                )
                currentPointDistance = calculateCurrentDistance(currentPoints)
                currentProfilePoints.removeAt(0)
            }
            currentDistances = calculateDistances(currentProfilePoints)
            totalDistances = currentDistances.sum()
            totalDistances += currentPointDistance

            destinationString = when {
                (currentProfile == "driving" || currentProfile == "bicycle") && !routeResponse.isNullOrEmpty() && routeResponse.size > 1 -> "Parking"
                currentProfile == "foot" && lineString.isNotEmpty() && lineString.size > 1 -> "Jeepney Stop"
                currentProfile == "transit" -> "Drop Off"
                else -> "Destination"
            }
            mode = when (currentProfile) {
                "foot" -> R.drawable.walking_icon  //Walking
                "bicycle" -> R.drawable.cycling_icon  // Blue for cycling
                "driving" -> R.drawable.car_icon  // Red for driving
                else -> R.mipmap.transit  // Black for any other type
            }
            turningDistance = currentPointDistance
            remainingDistance = totalDistances
            // Function to extract turning point directions and add them to the directions list
            extractTurningDirections()
            direction = directions[0]
            currentInstruction = if (direction == "Continue straight")
                "$direction for $turningDistance meters."
            else
                "$direction in $turningDistance meters."
            duration = when (currentProfile) {
                "foot" -> ((remainingDistance / walkingSpeed) / 60).toInt()
                "bicycle" -> ((remainingDistance / cyclingSpeed) / 60).toInt()
                "driving" -> ((remainingDistance / drivingSpeed) / 60).toInt()
                "transit" -> ((remainingDistance / transitSpeed) / 60).toInt()
                else -> 0
            }
            currentImage = getImage(direction)
        }
    }
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
            withContext(Dispatchers.IO) {
                if(currentPoints.isNotEmpty()) {
                    previousPointDistance = currentPointDistance
                    currentPoints.removeAt(0)

                    currentPoints.add(0, userLocation ?: GeoPoint(14.166995706659199, 121.24302756140631)) // add the userLocation
                    currentPointDistance = calculateCurrentDistance(currentPoints)

                    deviation += currentPointDistance - previousPointDistance
                    if (deviation >= 10) {
                        if(currentProfile != "transit") {
                            // Trigger recalculation if deviation exceeds +35 meters
                            getNewRoute(
                                context = context,
                                currentProfile = currentProfile,
                                userLocation = userLocation!!,
                                destinations = currentDestinations.firstOrNull() ?: mutableListOf()
                            ).let { newRoute ->
                                if (newRoute.isNotEmpty()) {
                                    lineString[0].second[0] = newRoute
                                    turningPoints = reduceToDirections(lineString)
                                    currentProfilePoints = getCurrentPoints(lineString, turningPoints)

                                    if (currentProfilePoints.isNotEmpty()) {
                                        currentPoints = currentProfilePoints[0]
                                        currentPoints.add(
                                            0,
                                            userLocation ?: GeoPoint(
                                                14.166995706659199,
                                                121.24302756140631
                                            )
                                        )
                                        currentPointDistance = calculateCurrentDistance(currentPoints)
                                        if(currentProfilePoints.isNotEmpty()) {
                                            currentProfilePoints.removeAt(0)
                                        }
                                        currentDistances = calculateDistances(currentProfilePoints)
                                        totalDistances = currentDistances.sum()
                                        totalDistances += currentPointDistance
                                    }
                                }
                            }
                        }else{
                            getNewTransitRoute(
                                context = context,
                                currentProfile = currentProfile,
                                userLocation = userLocation!!,
                                route = if(otherRouteString == "") "Forestry" else otherRouteString,
                                viewModel = mapViewModel,
                                destinations = currentDestinations.firstOrNull() ?: mutableListOf()
                            ).let { newRoute ->
                                if (newRoute.isNotEmpty()) {
                                    lineString[0].second[0] = newRoute
                                    turningPoints = reduceToDirections(lineString)
                                    currentProfilePoints = getCurrentPoints(lineString, turningPoints)
                                    if (currentProfilePoints.isNotEmpty()) {
                                        currentPoints = currentProfilePoints[0]
                                        currentPoints.add(
                                            0,
                                            userLocation ?: GeoPoint(
                                                14.166995706659199,
                                                121.24302756140631
                                            )
                                        )
                                        currentPointDistance = calculateCurrentDistance(currentPoints)
                                        if(currentProfilePoints.isNotEmpty()) {
                                            currentProfilePoints.removeAt(0)
                                        }
                                        currentDistances = calculateDistances(currentProfilePoints)
                                        totalDistances = currentDistances.sum()
                                        totalDistances += currentPointDistance
                                    }
                                }
                            }
                        }
                        extractTurningDirections()
                        direction = directions[0]
                        deviation = 0 // Reset deviation after recalculation
                    } else if (deviation <= -10) {
                        // Reset deviation if it's too negative
                        deviation = 0
                    }
                }
                turningDistance = currentPointDistance
                remainingDistance = currentDistances.sum() +currentPointDistance
                if(directions.isNotEmpty())
                    direction = directions[0]

                currentInstruction = if(direction == "Continue straight")
                    "$direction for \n$turningDistance meters."
                else
                    "$direction in \n$turningDistance meters."
                duration = when(currentProfile){
                    "foot" -> ((remainingDistance/walkingSpeed)/60).toInt()
                    "bicycle" -> ((remainingDistance/cyclingSpeed)/60).toInt()
                    "driving" -> ((remainingDistance/drivingSpeed)/60).toInt()
                    "transit" -> ((remainingDistance/transitSpeed)/60).toInt()
                    else -> 0
                }
                currentImage = getImage(direction)
            }


            if (turningPoints.isNotEmpty()) {
                val distanceToTurningPoint = haversineInMeters(
                    currentPoints[0].latitude,
                    currentPoints[0].longitude,
                    turningPoints[0].second.first().second.latitude,
                    turningPoints[0].second.first().second.longitude
                )

                if (distanceToTurningPoint <= 10) {
                    if(turningPoints.isNotEmpty()) {
                        turningPoints.removeAt(0)
                    }
                    if(directions.isNotEmpty()) {
                        directions.removeAt(0)
                    }
                    currentPoints = currentProfilePoints[0]
                    currentPoints.add(0, userLocation ?: GeoPoint(14.166995706659199, 121.24302756140631))
                    if(currentProfilePoints.isNotEmpty()) {
                        currentProfilePoints.removeAt(0)
                    }
                    currentDistances = calculateDistances(currentProfilePoints)

                    if (lineString.isNotEmpty() && lineString[0].second.isNotEmpty()) {
                        lineString[0].second[0].removeAt(0)
                    }

                    if (currentDistances.isNotEmpty()) {
                        currentDistances.removeAt(0)
                    }
                }
            }

            if (currentPoints.size > 1) {
                val distanceToNextPoint = haversineInMeters(
                    currentPoints[0].latitude,
                    currentPoints[0].longitude,
                    currentPoints[1].latitude,
                    currentPoints[1].longitude
                )
                if(lineString.isNotEmpty() && lineString[0].second.isNotEmpty() &&
                    turningPoints.isNotEmpty() && turningPoints[0].second.isNotEmpty()) {
                    if (distanceToNextPoint <= 20 && currentPoints[1] != turningPoints[0].second.first().second) {
                        currentPoints.removeAt(1)
                        lineString[0].second[0].removeAt(0)
                    }
                }
            }

            currentDistance = haversineInMeters(
                userLocation?.latitude ?: 14.166995706659199,
                userLocation?.longitude ?: 121.24302756140631,
                destinationLocation?.latitude ?: 14.162476817742977,
                destinationLocation?.longitude ?: 121.24091066262298
            )

            if(currentDistance < 5){
                currentInstruction = "You have arrived at your Destination: $title at $snippet"
                delay(5000L)
                navigateBack()
            }

            if(currentDestinations.isNotEmpty()) {
                val distance = haversineInMeters(
                    userLocation?.latitude ?: 14.166995706659199,
                    userLocation?.longitude ?: 121.24302756140631,
                    currentDestinations[0].first().latitude,
                    currentDestinations[0].first().longitude
                )
                if(distance < 5){
                    if(lineString.isNotEmpty()) {
                        lineString.removeAt(0)
                    }
                    if(currentDistances.isNotEmpty()) {
                        currentDistances.removeAt(0)
                    }
                    if(turningPoints.isNotEmpty()) {
                        turningPoints.removeAt(0)
                    }
                    if(directions.isNotEmpty()) {
                        directions.removeAt(0)
                    }
                    currentProfilePoints = getCurrentPoints(lineString, turningPoints)
                    if (currentProfilePoints.isNotEmpty()) {
                        currentPoints = currentProfilePoints[0]
                        currentPoints.add(
                            0,
                            userLocation ?: GeoPoint(
                                14.166995706659199,
                                121.24302756140631
                            )
                        )
                        currentPointDistance = calculateCurrentDistance(currentPoints)
                        if(currentProfilePoints.isNotEmpty()) {
                            currentProfilePoints.removeAt(0)
                        }
                        currentDistances = calculateDistances(currentProfilePoints)
                        totalDistances = currentDistances.sum()
                        totalDistances += currentPointDistance
                    }
                }
            }

            if(isNavigationEnabled) {
                val updateServiceIntent =
                    Intent(context, NavigationNotificationService::class.java).apply {
                        putExtra("title", title)
                        putExtra("currentInstruction", currentInstruction)
                    }
                context.startForegroundService(updateServiceIntent)
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
    val portraitWeight = .2f

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
                        .fillMaxHeight(portraitWeight),

                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = currentImage),
                        contentDescription = "Directions",
                        modifier = Modifier.size(48.dp)
                    )
                    Text(text = "$turningDistance meters, $deviation", color = Color.White, fontSize = 16.sp)
                }
                // Instructions
                Column(
                    modifier = Modifier
                        .padding(start = 0.dp, end = 0.dp, top = 0.dp, bottom = 0.dp)
                        .background(Color.Green)
                        .fillMaxHeight(portraitWeight)
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
                        .fillMaxHeight(portraitWeight),

                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = mode), // Mode of Transport
                        contentDescription = "Mode of Transport",
                        modifier = Modifier.size(24.dp),
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                    Text(text = "$remainingDistance meters\nand $duration \nminutes to\n$destinationString", color = Color.White, fontSize = 14.sp)

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
                    .fillMaxWidth(.2f),

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
                    .fillMaxHeight(.7f),

                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = mode), // Mode of Transport
                    contentDescription = "Mode of Transport",
                    modifier = Modifier.size(32.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
                Text(text = "$remainingDistance meters\n" +
                        "and $duration \n" +
                        "minutes to\n" +
                        destinationString, color = Color.White, fontSize = 14.sp)
            }
        }
    }




}
