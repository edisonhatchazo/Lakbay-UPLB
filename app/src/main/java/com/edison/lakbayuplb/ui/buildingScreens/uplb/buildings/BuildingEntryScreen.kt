package com.edison.lakbayuplb.ui.buildingScreens.uplb.buildings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edison.lakbayuplb.R
import com.edison.lakbayuplb.algorithm.MapSelectionDialog
import com.edison.lakbayuplb.ui.buildingScreens.uplb.BuildingDetails
import com.edison.lakbayuplb.ui.navigation.AppViewModelProvider
import com.edison.lakbayuplb.ui.navigation.NavigationDestination
import com.edison.lakbayuplb.ui.screen.EntryScreenTopAppBar
import com.edison.lakbayuplb.ui.settings.global.TopAppBarColorSchemesViewModel
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

object BuildingEntryDestination: NavigationDestination {
    override val route = "building_entry"
    override val titleRes = R.string.building_entry_title
}
@Composable
fun BuildingEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: BuildingEntryViewModel = viewModel(factory = AppViewModelProvider.Factory),
    colorViewModel: TopAppBarColorSchemesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val topAppBarColors = colorViewModel.topAppBarColors.collectAsState()
    val (topAppBarBackgroundColor, topAppBarForegroundColor) = topAppBarColors.value

    val coroutineScope = rememberCoroutineScope()
    val buildingUiState = viewModel.buildingUiState
    Scaffold(
        topBar = {
            EntryScreenTopAppBar(
                title = stringResource(BuildingEntryDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp,
                topAppBarBackgroundColor = topAppBarBackgroundColor,
                topAppBarForegroundColor = topAppBarForegroundColor
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
    enabled: Boolean = true
){
    var showMapDialog by remember { mutableStateOf(false) }
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
    var selectedLocation by remember { mutableStateOf(GeoPoint(buildingDetails.latitude, buildingDetails.longitude)) }


    LaunchedEffect(selectedLocation) {
        onValueChange(buildingDetails.copy(latitude = selectedLocation.latitude, longitude = selectedLocation.longitude))
    }

    LaunchedEffect(buildingDetails) {
        val newLocation = GeoPoint(buildingDetails.latitude, buildingDetails.longitude)
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
        OutlinedButton(
            onClick = { showMapDialog = true },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Select Location")
        }
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
    }
}
