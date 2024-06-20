package com.example.classschedule.ui.building.pins

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.classschedule.R
import com.example.classschedule.ui.navigation.AppViewModelProvider
import com.example.classschedule.ui.navigation.NavigationDestination
import com.example.classschedule.ui.screen.CoordinateEntryScreenTopAppBar
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.launch

object PinsEntryDestination: NavigationDestination {
    override val route = "pins_entry"
    override val titleRes = R.string.pin_entry_title
}

@Composable
fun PinsEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: PinsEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val pinsUiState = viewModel.pinsUiState
    var mapType by remember { mutableStateOf(MapType.NORMAL) }

    Scaffold(
        topBar = {
            CoordinateEntryScreenTopAppBar(
                title = stringResource(PinsEntryDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp,
                onMapTypeChange = { newMapType -> mapType = newMapType }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding()
                )
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        ) {
            PinsEntryBody(
                pinsUiState = pinsUiState,
                onPinsValueChange = viewModel::updateUiState,
                onSaveClick = {
                    coroutineScope.launch {
                        viewModel.savePin()
                        navigateBack()
                    }
                },
                mapType = mapType,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun PinsEntryBody(
    pinsUiState: PinsUiState,
    onPinsValueChange: (PinsDetails) -> Unit,
    onSaveClick: () -> Unit,
    mapType: MapType,
    modifier: Modifier = Modifier
){
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large)),
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        PinsInputForm(
            pinsDetails = pinsUiState.pinsDetails,
            onValueChange = onPinsValueChange,
            mapType = mapType,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            enabled = pinsUiState.isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.save_action))
        }
    }
}

@Composable
fun PinsInputForm(
    pinsDetails: PinsDetails,
    onValueChange: (PinsDetails) -> Unit,
    mapType: MapType,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
){
    var selectedLocation by remember { mutableStateOf(LatLng(pinsDetails.latitude, pinsDetails.longitude)) }
    val markerState = rememberMarkerState(position = selectedLocation)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(selectedLocation, 18f)
    }
    var properties by remember{
        mutableStateOf(
            MapProperties(
                isMyLocationEnabled = false,
                mapType = mapType
            )
        )
    }
    // Update pinsDetails when selectedLocation changes
    LaunchedEffect(selectedLocation) {
        onValueChange(pinsDetails.copy(latitude = selectedLocation.latitude, longitude = selectedLocation.longitude))
    }

    LaunchedEffect(pinsDetails) {
        val newLocation = LatLng(pinsDetails.latitude, pinsDetails.longitude)
        selectedLocation = newLocation
        cameraPositionState.position = CameraPosition.fromLatLngZoom(newLocation, 18f)
        markerState.position = newLocation
    }

    // Update map properties when mapType changes
    LaunchedEffect(mapType) {
        properties = properties.copy(mapType = mapType)
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        OutlinedTextField(
            value = pinsDetails.title,
            onValueChange = { onValueChange(pinsDetails.copy(title = it)) },
            label = { Text(stringResource(R.string.title)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = pinsDetails.floor,
            onValueChange = { onValueChange(pinsDetails.copy(floor = it)) },
            label = { Text(stringResource(R.string.floor)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )

        Box(
            modifier = Modifier.height(300.dp).fillMaxWidth()
        ) {

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = properties,
                onMapClick = { latLng ->
                    selectedLocation = latLng
                    markerState.position = latLng
                },
            ) {
                Marker(
                    state = markerState,
                    title = pinsDetails.title,
                    snippet = pinsDetails.title,
                    draggable = false // No need to make it draggable if we handle map clicks
                )
            }
        }


        if (enabled) {
            Text(
                text = stringResource(R.string.required_fields),
                modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_medium))
            )
        }
    }

}