package com.edison.lakbayuplb.ui.map



//
//@OptIn(ExperimentalPermissionsApi::class)
//@Composable
//fun OSMMap(
//    title: String,
//    snippet: String,
//    location: LatLng,
//    initialLocation: LatLng?,
//    routeViewModel: RouteSettingsViewModel,
//    destinationLocation: LatLng,
//    routeResponse: List<RouteWithLineString>?,
//    osmMapType: OSMCustomMapType,
//    modifier: Modifier = Modifier
//) {
//
//    val context = LocalContext.current
//    val permissionsState = rememberMultiplePermissionsState(
//        listOf(
//            Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.ACCESS_COARSE_LOCATION
//        )
//    )
//
//    val walkingSpeed by routeViewModel.walkingSpeed.collectAsState()
//    val cyclingSpeed by routeViewModel.cyclingSpeed.collectAsState()
//    val carSpeed by routeViewModel.carSpeed.collectAsState()
//    val jeepneySpeed by routeViewModel.jeepneySpeed.collectAsState()
//
//
//    val minZoom = 14.0
//    val maxZoom = 19.0
//    val bounds = LatLngBounds.Builder()
//        .include(LatLng(14.147,  121.227)) // Southwest corner
//        .include(LatLng(14.178, 121.121)) // Northeast corner
//        .build()
//
////    val bounds = LatLngBounds.Builder()
////        .include(LatLng(14.116059432252356,  121.29498816252921)) // Southwest corner
////        .include(LatLng(14.18336407476095, 121.19205689274669)) // Northeast corner
////        .build()
//    var mapViewKey by remember { mutableIntStateOf(0) }
//    LaunchedEffect(Unit) {
//        permissionsState.launchMultiplePermissionRequest()
//    }
//
//    if (permissionsState.allPermissionsGranted) {
//        var mapView by remember { mutableStateOf<MapView?>(null) }
//        var symbolManager by remember { mutableStateOf<SymbolManager?>(null) }
//        var symbols by remember { mutableStateOf<List<Symbol>>(emptyList()) }
//
//        MapLibre.getInstance(context)
//        DisposableEffect(Unit) {
//            onDispose {
//                mapView?.onStop()
//                mapView?.onDestroy()
//                mapView = null
//                symbolManager?.let {
//                    it.delete(symbols)
//                    it.onDestroy()
//                }
//                symbols = emptyList()
//                symbolManager = null
//            }
//        }
//
//        LaunchedEffect(key1 = routeResponse,key2 = osmMapType) {
//            symbolManager?.let {
//                it.delete(symbols)
//                symbols = emptyList()
//                mapViewKey++ // Force map view reset
//            }
//            routeResponse?.forEach { routeWithLineString ->
//                if(routeWithLineString.lineString!="") {
//                    Log.d("OSMMainMap", "Bus Route Name: ${routeWithLineString.lineString}")
//                }
//            }
//        }
//
//        LaunchedEffect(mapViewKey) {
//            mapView?.getMapAsync { mapLibreMap ->
//                mapLibreMap.setStyle(osmMapType.styleUrl) { style ->
//
//                    val manager = SymbolManager(mapView!!, mapLibreMap, style).apply {
//                        iconAllowOverlap = true
//                        textAllowOverlap = true
//                    }
//                    symbolManager = manager
//
//                    style.addImage("destination-icon", BitmapFactory.decodeResource(context.resources, R.drawable.marker_48))
//                    style.addImage("initial-icon", BitmapFactory.decodeResource(context.resources, R.drawable.icons8_circle_18___))
//                    style.addImage("walking-icon", BitmapFactory.decodeResource(context.resources, R.drawable.walking_icon))
//                    style.addImage("cycling-icon", BitmapFactory.decodeResource(context.resources, R.drawable.cycling_icon))
//                    style.addImage("car-icon", BitmapFactory.decodeResource(context.resources, R.drawable.car_icon))
//                    style.addImage("transit-icon", BitmapFactory.decodeResource(context.resources, R.mipmap.transit))
//
//                    mapLibreMap.setMinZoomPreference(minZoom)
//                    mapLibreMap.setMaxZoomPreference(maxZoom)
//                    mapLibreMap.setLatLngBoundsForCameraTarget(bounds)
//                    val cameraLocation = if (routeResponse.isNullOrEmpty()) {
//                        location
//                    } else {
//                        initialLocation ?: location
//                    }
//                    mapLibreMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraLocation, 18.0))
//
//                    initialLocation?.let {
//                        val bearing = destinationLocation?.let { dest ->
//                            calculateBearing(it, dest)
//                        } ?: 0f
//                        mapLibreMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 18.0))
//                        mapLibreMap.animateCamera(CameraUpdateFactory.bearingTo(bearing.toDouble()))
//                    }
//
//                    if(initialLocation != null && destinationLocation != null) {
//                        symbols = listOf(
//                            manager.create(
//                                SymbolOptions()
//                                    .withLatLng(destinationLocation)
//                                    .withIconImage("destination-icon")
//                                    .withTextField(title)
//                                    .withTextOffset(arrayOf(0f, 1.5f))
//                            ),
//                            manager.create(
//                                SymbolOptions()
//                                    .withLatLng(initialLocation)
//                                    .withIconImage("initial-icon")
//                                    .withTextField("Your Location")
//                                    .withTextOffset(arrayOf(0f, 1.5f))
//                            )
//                        )
//                    }
//
//                    routeResponse?.forEachIndexed { index, (route, color) ->
//                        val coordinates = route.routes.first().legs.flatMap { leg ->
//                            leg.steps.flatMap { step ->
//                                PolylineUtils.decode(step.geometry, 5)
//                            }
//                        }.map {
//                            Point.fromLngLat(it.longitude(), it.latitude())
//                        }
//
//                        val distance = route.routes.first().distance // in meters
//                        val selectedSpeed = when (routeResponse[index].profile) {
//                            "foot" -> walkingSpeed
//                            "bicycle" -> cyclingSpeed
//                            "car" -> carSpeed
//                            "transit" -> jeepneySpeed
//                            else -> walkingSpeed // Default to car speed if route type is unknown
//                        }
//                        val duration = distance / selectedSpeed / 60 // in minutes
//
//                        val midpoint = coordinates[coordinates.size / 2]
//                        val adjustedMidpoint = LatLng(midpoint.latitude() + 0.0002, midpoint.longitude() - 0.0002)
//
//                        val iconName = when (color) {
//                            "#0000FF" -> "walking-icon" // Blue for walking
//                            "#FFA500" -> "cycling-icon" // Orange for cycling
//                            "#FF0000" -> "car-icon" // Red for car
//                            "#00FF00" -> "transit-icon" // Green for transit
//                            else -> "default-icon"
//                        }
//
//
//                        symbols += manager.create(
//                            SymbolOptions()
//                                .withLatLng(adjustedMidpoint)
//                                .withIconImage(iconName)
//                                .withTextField("${duration.toInt()} min\n${distance.format(2)} m")
//                                .withTextOffset(arrayOf(0f, 1.5f))
//                                .withTextJustify("auto")
//                                .withTextAnchor("top")
//                        )
//
//                        val routeFeature = Feature.fromGeometry(LineString.fromLngLats(coordinates))
//                        val routeSource = GeoJsonSource("route-source-$index", FeatureCollection.fromFeature(routeFeature))
//                        style.addSource(routeSource)
//
//                        val routeLayer = LineLayer("route-layer-$index", "route-source-$index").apply {
//                            setProperties(
//                                PropertyFactory.lineColor(Color.parseColor(color)),
//                                PropertyFactory.lineWidth(5.0f)
//                            )
//                        }
//                        style.addLayer(routeLayer)
//
//                    }
//
//
//                }
//            }
//        }
//
//        AndroidView(
//            modifier = modifier.fillMaxSize(),
//            factory = {
//                MapView(context).apply {
//                    mapView = this
//                }
//            },
//            update = {
//                mapView = it
//            }
//        )
//    } else {
//        if (permissionsState.shouldShowRationale) {
//            AlertDialog(
//                onDismissRequest = { /* Do nothing */ },
//                title = { Text("Permissions Required") },
//                text = { Text("This app requires location permissions to show your position on the map.") },
//                confirmButton = {
//                    TextButton(onClick = { permissionsState.launchMultiplePermissionRequest() }) {
//                        Text("OK")
//                    }
//                }
//            )
//        }
//    }
//}
//
