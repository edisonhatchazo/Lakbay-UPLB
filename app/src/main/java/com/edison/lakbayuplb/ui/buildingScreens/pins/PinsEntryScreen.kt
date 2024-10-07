package com.edison.lakbayuplb.ui.buildingScreens.pins

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edison.lakbayuplb.R
import com.edison.lakbayuplb.algorithm.ColorPickerDialog
import com.edison.lakbayuplb.ui.navigation.AppViewModelProvider
import com.edison.lakbayuplb.ui.navigation.NavigationDestination
import com.edison.lakbayuplb.ui.screen.CoordinateEntryScreenTopAppBar
import com.edison.lakbayuplb.ui.settings.global.TopAppBarColorSchemesViewModel
import kotlinx.coroutines.launch
import org.maplibre.android.geometry.LatLng

object PinsEntryDestination: NavigationDestination {
    override val route = "pins_entry"
    override val titleRes = R.string.pin_entry_title
}

@Composable
fun PinsEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: PinsEntryViewModel = viewModel(factory = AppViewModelProvider.Factory),
    colorViewModel: TopAppBarColorSchemesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val topAppBarColors = colorViewModel.topAppBarColors.collectAsState()
    val (topAppBarBackgroundColor, topAppBarForegroundColor) = topAppBarColors.value

    val coroutineScope = rememberCoroutineScope()
    val pinsUiState = viewModel.pinsUiState

    Scaffold(
        topBar = {
            CoordinateEntryScreenTopAppBar(
                title = stringResource(PinsEntryDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp,
                topAppBarForegroundColor = topAppBarForegroundColor,
                topAppBarBackgroundColor = topAppBarBackgroundColor
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
    modifier: Modifier = Modifier
){
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large)),
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        PinsInputForm(
            pinsDetails = pinsUiState.pinsDetails,
            onValueChange = onPinsValueChange,
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
    viewModel:PinsEntryViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier = Modifier,
    enabled: Boolean = true
){
    var selectedLocation by remember { mutableStateOf(LatLng(pinsDetails.latitude, pinsDetails.longitude)) }
    var showColorPicker by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val colorEntry = viewModel.colorSchemesUiState
    val coroutineScope = rememberCoroutineScope()
    // Update pinsDetails when selectedLocation changes
    LaunchedEffect(selectedLocation) {
        onValueChange(pinsDetails.copy(latitude = selectedLocation.latitude, longitude = selectedLocation.longitude))
    }

    LaunchedEffect(pinsDetails) {
        val newLocation = LatLng(pinsDetails.latitude, pinsDetails.longitude)
        selectedLocation = newLocation
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

//        Box(
//            modifier = Modifier
//                .height(300.dp)
//                .fillMaxWidth()
//        ) {
//            AndroidView(
//                modifier = Modifier.fillMaxSize(),
//                factory = { context ->
//                    MapLibre.getInstance(context, apiKey, tileServer)
//                    MapView(context).apply {
//                        getMapAsync { mapLibreMap ->
//                            mapLibreMap.setStyle(mapType.styleUrl) { style ->
//                                val location = LatLng(pinsDetails.latitude, pinsDetails.longitude)
//                                mapLibreMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 18.0))
//
//                                // Add the marker icon to the style
//                                style.addImage("marker-icon", BitmapFactory.decodeResource(context.resources, R.drawable.marker_48))
//
//                                // Initialize SymbolManager
//                                val symbolManager = SymbolManager(this, mapLibreMap, style).apply {
//                                    iconAllowOverlap = true
//                                    textAllowOverlap = true
//                                }
//
//                                // Add a marker (symbol)
//                                val symbolOptions = SymbolOptions()
//                                    .withLatLng(location)
//                                    .withIconImage("marker-icon")
//                                    .withTextField(pinsDetails.title)
//                                    .withTextOffset(arrayOf(0f, 1.5f))
//                                symbolManager.create(symbolOptions)
//
//                                mapLibreMap.addOnMapClickListener { latLng ->
//                                    selectedLocation = latLng
//                                    symbolManager.deleteAll()
//                                    symbolManager.create(
//                                        SymbolOptions()
//                                            .withLatLng(latLng)
//                                            .withIconImage("marker-icon")
//                                            .withTextField(pinsDetails.title)
//                                            .withTextOffset(arrayOf(0f, 1.5f))
//                                    )
//                                    true
//                                }
//                            }
//                        }
//                    }
//                }
//            )
//        }

        OutlinedButton(
            onClick = { showColorPicker = true },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(color = Color(colorEntry.colorSchemeDetails.backgroundColor))
        ) {
            Text("Select Color", color = Color(colorEntry.colorSchemeDetails.fontColor))
        }


        if (enabled) {
            Text(
                text = stringResource(R.string.required_fields),
                modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_medium))
            )
        }
    }

    if (showColorPicker) {
        ColorPickerDialog(
            onColorSelected = { colorId ->
                onValueChange(pinsDetails.copy(colorId = colorId))
                coroutineScope.launch{
                    viewModel.getColor(colorId)
                }
                showColorPicker = false
            },
            onDismiss = { showColorPicker = false }
        )
    }

}