package com.example.classschedule.ui.buildingScreens.uplb

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
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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

object RoomDetailsDestination : NavigationDestination {
    override val route = "room_details"
    override val titleRes = R.string.room_detail_title
    const val ROOMIDARG = "roomId"
    val routeWithArgs = "$route/{$ROOMIDARG}"
}

@Composable
fun RoomDetailsScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RoomDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val roomUiState by viewModel.classroomUiState.collectAsState()
    val room = roomUiState.classroomDetails
    var mapType by remember { mutableStateOf(MapType.NORMAL) }
    Scaffold(
        topBar = {
            CoordinateEntryScreenTopAppBar(
                title = room.abbreviation,
                canNavigateBack = true,
                navigateUp = navigateBack,
                onMapTypeChange = { newMapType -> mapType = newMapType }
            )
        }, modifier = modifier
    ){innerPadding ->
        ClassroomDetailsBody(
            mapType = mapType,
            classroomDetails = room,
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
private fun ClassroomDetailsBody(
    classroomDetails: ClassroomDetails,
    mapType: MapType,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {

        ClassroomDetailed(
            classroom = classroomDetails,
            modifier = Modifier.fillMaxWidth(),
            mapType = mapType
        )

    }
}

@Composable
fun ClassroomDetailed(
    classroom: ClassroomDetails,
    modifier: Modifier = Modifier,
    mapType: MapType
){
    val selectedLocation = remember(classroom.latitude,classroom.longitude){ LatLng(classroom.latitude, classroom.longitude) }
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
    LaunchedEffect(classroom) {
        val newLocation = LatLng(classroom.latitude, classroom.longitude)
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
            ClassroomDetailsRow(
                labelResID = R.string.title,
                classroomDetail = classroom.title,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                )
            )
            ClassroomDetailsRow(
                labelResID = R.string.floor,
                classroomDetail = classroom.floor,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                )
            )

        }
    }
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
                title = classroom.title,
                snippet = classroom.title,
                draggable = false // No need to make it draggable if we handle map clicks
            )
        }
    }
}

@Composable
private fun ClassroomDetailsRow(
    @StringRes labelResID: Int, classroomDetail: String, modifier: Modifier = Modifier
){
    Row(modifier = modifier) {
        Text(stringResource(labelResID))
        Spacer(modifier = Modifier.weight(1f))
        Text(text = classroomDetail, fontWeight = FontWeight.Bold)
    }
}

