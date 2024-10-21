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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edison.lakbayuplb.R
import com.edison.lakbayuplb.algorithm.ColorPickerDialog
import com.edison.lakbayuplb.algorithm.MapSelectionDialog
import com.edison.lakbayuplb.ui.navigation.AppViewModelProvider
import com.edison.lakbayuplb.ui.navigation.NavigationDestination
import com.edison.lakbayuplb.ui.screen.CoordinateEntryScreenTopAppBar
import com.edison.lakbayuplb.ui.settings.global.TopAppBarColorSchemesViewModel
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

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
    modifier: Modifier = Modifier,
    pinsDetails: PinsDetails,
    onValueChange: (PinsDetails) -> Unit,
    viewModel: PinsEntryViewModel = viewModel(factory = AppViewModelProvider.Factory),
    enabled: Boolean = true
) {
    var selectedLocation by remember { mutableStateOf(GeoPoint(pinsDetails.latitude, pinsDetails.longitude)) }
    var showMapDialog by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }

    val colorEntry = viewModel.colorSchemesUiState
    val coroutineScope = rememberCoroutineScope()

    // Update pinsDetails when selectedLocation changes
    LaunchedEffect(selectedLocation) {
        onValueChange(pinsDetails.copy(latitude = selectedLocation.latitude, longitude = selectedLocation.longitude))
    }

    // Sync selectedLocation with pinsDetails when pinsDetails changes
    LaunchedEffect(pinsDetails) {
        selectedLocation = GeoPoint(pinsDetails.latitude, pinsDetails.longitude)
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

        // Button to open the map dialog for location selection
        OutlinedButton(
            onClick = { showMapDialog = true },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Select Location")
        }

        // Button to open color picker
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

        // Required fields label
        if (enabled) {
            Text(
                text = stringResource(R.string.required_fields),
                modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_medium))
            )
        }
    }

    // Display the map dialog when the button is clicked
    if (showMapDialog) {
        MapSelectionDialog(
            initialLocation = selectedLocation,
            onLocationSelected = { geoPoint ->
                selectedLocation = geoPoint
                showMapDialog = false
            },
            onDismiss = { showMapDialog = false }
        )
    }

    // Display the color picker dialog when the button is clicked
    if (showColorPicker) {
        ColorPickerDialog(
            onColorSelected = { colorId ->
                onValueChange(pinsDetails.copy(colorId = colorId))
                coroutineScope.launch {
                    viewModel.getColor(colorId)
                }
                showColorPicker = false
            },
            onDismiss = { showColorPicker = false }
        )
    }
}
