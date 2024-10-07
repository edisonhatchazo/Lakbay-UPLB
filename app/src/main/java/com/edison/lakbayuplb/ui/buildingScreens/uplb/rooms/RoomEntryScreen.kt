package com.edison.lakbayuplb.ui.buildingScreens.uplb.rooms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edison.lakbayuplb.R
import com.edison.lakbayuplb.ui.buildingScreens.uplb.ClassroomDetails
import com.edison.lakbayuplb.ui.navigation.AppViewModelProvider
import com.edison.lakbayuplb.ui.navigation.NavigationDestination
import com.edison.lakbayuplb.ui.screen.EntryScreenTopAppBar
import com.edison.lakbayuplb.ui.settings.global.TopAppBarColorSchemesViewModel
import kotlinx.coroutines.launch
import org.maplibre.android.geometry.LatLng

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

    var selectedLocation by remember { mutableStateOf(LatLng(classroomDetails.latitude, classroomDetails.longitude)) }

    val context = LocalContext.current

    LaunchedEffect(selectedLocation) {
        onValueChange(classroomDetails.copy(latitude = selectedLocation.latitude, longitude = selectedLocation.longitude))
    }

    LaunchedEffect(classroomDetails) {
        val newLocation = LatLng(classroomDetails.latitude, classroomDetails.longitude)
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
//                                val location = LatLng(classroomDetails.latitude, classroomDetails.longitude)
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
//                                    .withTextField(classroomDetails.title)
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
//                                            .withTextField(classroomDetails.title)
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

    }
}





