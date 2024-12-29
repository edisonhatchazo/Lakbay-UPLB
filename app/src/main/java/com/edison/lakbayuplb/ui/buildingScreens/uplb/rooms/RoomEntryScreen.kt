package com.edison.lakbayuplb.ui.buildingScreens.uplb.rooms

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
import com.edison.lakbayuplb.ui.buildingScreens.uplb.ClassroomDetails
import com.edison.lakbayuplb.ui.navigation.AppViewModelProvider
import com.edison.lakbayuplb.ui.navigation.NavigationDestination
import com.edison.lakbayuplb.ui.screen.EntryScreenTopAppBar
import com.edison.lakbayuplb.ui.settings.global.TopAppBarColorSchemesViewModel
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

object ClassroomEntryDestination : NavigationDestination {
    override val route = "classroom_entry"
    override val titleRes = R.string.room_entry_title
    const val BUILDINGIDARG = "buildingId"
    val routeWithArgs = "$route/{$BUILDINGIDARG}"
}

@Composable
fun RoomEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    navigateToAboutPage: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: RoomEntryViewModel = viewModel(factory = AppViewModelProvider.Factory),
    colorViewModel: TopAppBarColorSchemesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val topAppBarColors = colorViewModel.topAppBarColors.collectAsState()
    val (topAppBarBackgroundColor, topAppBarForegroundColor) = topAppBarColors.value

    val coroutineScope = rememberCoroutineScope()
    val buildingUiState = viewModel.buildingUiState.collectAsState()
    val classroomUiState = viewModel.classroomUiState
    Scaffold(
        topBar = {
            EntryScreenTopAppBar(
                title = stringResource(ClassroomEntryDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp,
                navigateToAboutPage = navigateToAboutPage,
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
            ClassroomEntryBody(
                buildingId = buildingUiState.value.buildingDetails.buildingId,
                college = buildingUiState.value.buildingDetails.college,
                classroomUiState = classroomUiState,
                onSaveClick = {
                    coroutineScope.launch {
                        viewModel.saveClassroom()
                        navigateBack()
                    }
                },
                onClassroomValueChange = viewModel::updateUiState,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ClassroomEntryBody(
    buildingId: Int,
    college: String,
    classroomUiState: ClassroomUiState,
    onSaveClick: () -> Unit,
    onClassroomValueChange:  (ClassroomDetails) -> Unit,
    modifier: Modifier
){
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large)),
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        ClassroomInputForm(
            buildingId = buildingId,
            college = college,
            classroomDetails = classroomUiState.classroomDetails,
            onValueChange = { onClassroomValueChange(it) },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            enabled = classroomUiState.isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.save_action))
        }
    }
}

@Composable
fun ClassroomInputForm(
    buildingId: Int,
    college: String,
    classroomDetails: ClassroomDetails,
    onValueChange: (ClassroomDetails)-> Unit,
    modifier: Modifier,
    enabled: Boolean = true
){
    val types = listOf(
        "Room",	"Gallery", "Office", "Library","Institute",	"Department"
    )
    var showMapDialog by remember { mutableStateOf(false) }
    var selectedLocation by remember { mutableStateOf(GeoPoint(classroomDetails.latitude, classroomDetails.longitude)) }

    LaunchedEffect(selectedLocation) {
        onValueChange(classroomDetails.copy(latitude = selectedLocation.latitude, longitude = selectedLocation.longitude))
    }

    LaunchedEffect(classroomDetails) {
        val newLocation = GeoPoint(classroomDetails.latitude, classroomDetails.longitude)
        selectedLocation = newLocation
    }
    var selectedTypes by remember { mutableStateOf(classroomDetails.type) }
    var expanded by remember {mutableStateOf(false)}

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {

        OutlinedTextField(
            value = classroomDetails.title,
            onValueChange = {onValueChange(classroomDetails.copy(
                title = it,
                buildingId = buildingId,
                college = college))},
            label = {Text(stringResource(R.string.title))},
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = classroomDetails.floor,
            onValueChange = {onValueChange(classroomDetails.copy(floor = it))},
            modifier = Modifier.fillMaxWidth(),
            label = {Text(stringResource(R.string.floor))},
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = classroomDetails.abbreviation,
            onValueChange = {onValueChange(classroomDetails.copy(abbreviation = it))},
            modifier = Modifier.fillMaxWidth(),
            label = {Text(stringResource(R.string.abbreviation))},
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = classroomDetails.college,
            label = { Text("College") },
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
            singleLine = true
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.TopStart)
        ) {
            OutlinedTextField(
                value = selectedTypes,
                onValueChange = {
                    selectedTypes = it
                    expanded = true
                    onValueChange(classroomDetails.copy(type = it))
                },
                label = { Text(stringResource(R.string.type)) },
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
                types.filter {
                    it.contains(selectedTypes, ignoreCase = true)
                }.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(text = type) },
                        onClick = {
                            selectedTypes = type
                            onValueChange(classroomDetails.copy(type = type))
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





