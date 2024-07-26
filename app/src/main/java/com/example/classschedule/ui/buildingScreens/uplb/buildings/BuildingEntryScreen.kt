package com.example.classschedule.ui.buildingScreens.uplb.buildings

import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.classschedule.R
import com.example.classschedule.ui.buildingScreens.uplb.BuildingDetails
import com.example.classschedule.ui.map.OSMCustomMapType
import com.example.classschedule.ui.navigation.AppViewModelProvider
import com.example.classschedule.ui.navigation.NavigationDestination
import com.example.classschedule.ui.screen.EntryScreenTopAppBar
import kotlinx.coroutines.launch
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.plugins.annotation.SymbolManager
import org.maplibre.android.plugins.annotation.SymbolOptions

object BuildingEntryDestination: NavigationDestination {
    override val route = "building_entry"
    override val titleRes = R.string.building_entry_title
}
@Composable
fun BuildingEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: BuildingEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val buildingUiState = viewModel.buildingUiState
    var mapType by remember { mutableStateOf(OSMCustomMapType.STREET) }
    Scaffold(
        topBar = {
            EntryScreenTopAppBar(
                title = stringResource(BuildingEntryDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                )
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        ) {
            BuildingEntryBody(
                buildingUiState = buildingUiState,
                mapType = mapType,
                onSaveClick = {
                    coroutineScope.launch {
                        viewModel.saveBuilding()
                        navigateBack()
                    }
                },
                onBuildingValueChange = viewModel::updateUiState,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun BuildingEntryBody(
    buildingUiState: BuildingUiState,
    onSaveClick: () -> Unit,
    mapType: OSMCustomMapType,
    onBuildingValueChange: (BuildingDetails) -> Unit,
    modifier: Modifier
){
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large)),
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        BuildingInputForm(
            buildingDetails = buildingUiState.buildingDetails,
            onValueChange = { onBuildingValueChange(it) },
            mapType = mapType,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            enabled = buildingUiState.isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.save_action))
        }
    }
}

@Composable
fun BuildingInputForm(
    buildingDetails: BuildingDetails,
    onValueChange: (BuildingDetails)-> Unit,
    modifier: Modifier,
    mapType: OSMCustomMapType,
    enabled: Boolean = true
){
    val colleges = listOf(
        "College of Arts and Sciences",
        "College of Development Communication",
        "College of Agriculture",
        "College of Veterinary Medicine",
        "College of Human Ecology",
        "College of Public Affairs and Development",
        "College of Forestry and Natural Resources",
        "College of Economics and Management",
        "College of Engineering and Agro-industrial Technology",
        "Graduate School",
        "UP Unit",
        "Dormitory",
        "Landmark"
    )
    var selectedLocation by remember { mutableStateOf(LatLng(buildingDetails.latitude, buildingDetails.longitude)) }

    val context = LocalContext.current
    val apiKey = context.getString(R.string.kento)
    val tileServer: WellKnownTileServer = WellKnownTileServer.MapLibre

    LaunchedEffect(selectedLocation) {
        onValueChange(buildingDetails.copy(latitude = selectedLocation.latitude, longitude = selectedLocation.longitude))
    }

    LaunchedEffect(buildingDetails) {
        val newLocation = LatLng(buildingDetails.latitude, buildingDetails.longitude)
        selectedLocation = newLocation
    }

    var selectedCollege by remember { mutableStateOf(buildingDetails.college) }
    var expanded by remember {mutableStateOf(false)}

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        OutlinedTextField(
            value = buildingDetails.name,
            onValueChange = {onValueChange(buildingDetails.copy(name = it))},
            label = {Text(stringResource(R.string.name))},
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = buildingDetails.otherName ?: "",
            onValueChange = {onValueChange(buildingDetails.copy(otherName = it))},
            label = {Text(stringResource(R.string.other_name_null))},
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = buildingDetails.abbreviation,
            onValueChange = {onValueChange(buildingDetails.copy(abbreviation = it))},
            label = {Text(stringResource(R.string.abbreviation))},
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.TopStart)
        ) {
            OutlinedTextField(
                value = selectedCollege,
                onValueChange = {
                    selectedCollege = it
                    expanded = true
                    onValueChange(buildingDetails.copy(college = it))
                },
                label = { Text(stringResource(R.string.college_unit)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
                singleLine = true,
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                colleges.filter {
                    it.contains(selectedCollege, ignoreCase = true)
                }.forEach { college ->
                    DropdownMenuItem(
                        text = { Text(text = college) },
                        onClick = {
                            selectedCollege = college
                            onValueChange(buildingDetails.copy(college = college))
                            expanded = false
                        }
                    )
                }
            }
        }

        Box(
            modifier = Modifier.height(300.dp).fillMaxWidth()
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    MapLibre.getInstance(context, apiKey, tileServer)
                    MapView(context).apply {
                        getMapAsync { mapLibreMap ->
                            mapLibreMap.setStyle(mapType.styleUrl) { style ->
                                val location = LatLng(buildingDetails.latitude, buildingDetails.longitude)
                                mapLibreMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 18.0))

                                // Add the marker icon to the style
                                style.addImage("marker-icon", BitmapFactory.decodeResource(context.resources, R.drawable.marker_48))

                                // Initialize SymbolManager
                                val symbolManager = SymbolManager(this, mapLibreMap, style).apply {
                                    iconAllowOverlap = true
                                    textAllowOverlap = true
                                }

                                // Add a marker (symbol)
                                val symbolOptions = SymbolOptions()
                                    .withLatLng(location)
                                    .withIconImage("marker-icon")
                                    .withTextField(buildingDetails.name)
                                    .withTextOffset(arrayOf(0f, 1.5f))
                                symbolManager.create(symbolOptions)

                                mapLibreMap.addOnMapClickListener { latLng ->
                                    selectedLocation = latLng
                                    symbolManager.deleteAll()
                                    symbolManager.create(
                                        SymbolOptions()
                                            .withLatLng(latLng)
                                            .withIconImage("marker-icon")
                                            .withTextField(buildingDetails.name)
                                            .withTextOffset(arrayOf(0f, 1.5f))
                                    )
                                    true
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}
