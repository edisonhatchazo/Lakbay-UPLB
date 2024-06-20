package com.example.classschedule.ui.building.pins

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.classschedule.R
import com.example.classschedule.data.Pins
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

object PinsDetailsDestination : NavigationDestination {
    override val route = "pins_details"
    override val titleRes = R.string.pin_detail_title
    const val PINIDARG = "PinId"
    val routeWithArgs = "$route/{$PINIDARG}"
}
@Composable
fun PinsDetailsScreen(
    navigateToEditPin: (Int) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PinsDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var mapType by remember { mutableStateOf(MapType.NORMAL) }

    Scaffold(
        topBar = {
            CoordinateEntryScreenTopAppBar(
                title = stringResource(PinsDetailsDestination.titleRes),
                canNavigateBack = true,
                navigateUp = navigateBack,
                onMapTypeChange = { newMapType -> mapType = newMapType }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateToEditPin(uiState.pinsDetails.id) },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit_pin_title)
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        PinsDetailsBody(
            pinsDetailsUiState = uiState,
            onDelete = {
                coroutineScope.launch {
                    viewModel.deletePin()
                    navigateBack()
                }
            },
            mapType = mapType,
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding()
                )
                .verticalScroll(rememberScrollState())
        )
    }
}

@Composable
private fun PinsDetailsBody(
    pinsDetailsUiState: PinsDetailsUiState,
    onDelete: () -> Unit,
    mapType: MapType,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }

        PinDetails(
            pin = pinsDetailsUiState.pinsDetails.toPins(),
            modifier = Modifier.fillMaxWidth(),
            mapType = mapType
        )
        OutlinedButton(
            onClick = { deleteConfirmationRequired = true },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.delete))
        }
        if (deleteConfirmationRequired) {
            DeleteConfirmationDialog(
                onDeleteConfirm = {
                    deleteConfirmationRequired = false
                    onDelete()
                },
                onDeleteCancel = { deleteConfirmationRequired = false },
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
            )
        }
    }

}

@Composable
fun PinDetails(
    pin: Pins, modifier: Modifier = Modifier, mapType: MapType,
) {
    val selectedLocation = remember(pin.latitude, pin.longitude) { LatLng(pin.latitude, pin.longitude) }
    val markerState = rememberMarkerState(position = selectedLocation)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(selectedLocation, 18f)
    }
    var properties by remember {
        mutableStateOf(
            MapProperties(
                isMyLocationEnabled = false,
                mapType = mapType
            )
        )
    }

    LaunchedEffect(pin) {
        val newLocation = LatLng(pin.latitude, pin.longitude)
        markerState.position = newLocation
        cameraPositionState.position = CameraPosition.fromLatLngZoom(newLocation, 18f)
    }

    LaunchedEffect(mapType) {
        properties = properties.copy(mapType = mapType)
    }
    Card(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.padding_medium)
            )
        ) {
            PinDetailsRow(
                labelResID = R.string.my_pins,
                pinDetail = pin.title,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                )
            )
            PinDetailsRow(
                labelResID = R.string.floor,
                pinDetail = pin.floor,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                )
            )

        }
    }
    Text(pin.latitude.toString())
    Text(pin.longitude.toString())
    Box(
        modifier = Modifier.height(300.dp).fillMaxWidth()
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = properties,
        ) {
            Marker(
                state = markerState,
                title = pin.title,
                snippet = pin.title,
                draggable = false // No need to make it draggable if we handle map clicks
            )
        }
    }
}
@Composable
private fun PinDetailsRow(
    @StringRes labelResID: Int, pinDetail: String, modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(stringResource(labelResID))
        Spacer(modifier = Modifier.weight(1f))
        Text(text = pinDetail, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = { /*Do Nothing*/ },
        title = { Text(stringResource(R.string.delete_question)) },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text(stringResource(R.string.no))
            }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Text(stringResource(R.string.yes))
            }
        }
    )
}