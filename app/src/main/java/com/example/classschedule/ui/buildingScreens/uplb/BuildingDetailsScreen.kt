package com.example.classschedule.ui.buildingScreens.uplb

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.classschedule.R
import com.example.classschedule.data.Building
import com.example.classschedule.data.Classroom
import com.example.classschedule.ui.map.OSMCustomMapType
import com.example.classschedule.ui.map.OSMMapping
import com.example.classschedule.ui.navigation.AppViewModelProvider
import com.example.classschedule.ui.navigation.NavigationDestination
import com.example.classschedule.ui.screen.CoordinateEntryScreenTopAppBar
import com.example.classschedule.ui.screen.DetailsScreenTopAppBar
import com.example.classschedule.ui.theme.CollegeColorPalette
import kotlinx.coroutines.launch

object BuildingDetailsDestination : NavigationDestination {
    override val route = "building_details"
    override val titleRes = R.string.building_detail_title
    const val BUILDINGIDARG = "buildingId"
    val routeWithArgs = "$route/{$BUILDINGIDARG}"
}

@Composable
fun BuildingDetailsScreen (
    navigateToRoomDetails: (Int) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BuildingDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateToMap: (Int) -> Unit,
){
    var mapType by remember { mutableStateOf(OSMCustomMapType.STREET) }
    val roomUiState by viewModel.buildingRoomUiState.collectAsState()
    val room = roomUiState.roomList
    val uiState = viewModel.uiState.collectAsState()
    val build = uiState.value.buildingDetails.toBuilding()

    Scaffold(
        topBar = {
            if(build.college == "Landmark" || build.college == "Dormitory" || build.college == "UP unit"){
                CoordinateEntryScreenTopAppBar(
                    title = build.name,
                    canNavigateBack = true,
                    navigateUp = navigateBack
                )
            }
            else {
                DetailsScreenTopAppBar(
                    title = build.name,
                    canNavigateBack = true,
                    navigateUp = navigateBack
                )
            }
        }, modifier = modifier
    ){innerPadding ->
        BuildingDetailsBody(
            mapType = mapType,
            navigateToRoomDetails = navigateToRoomDetails,
            buildingDetailsUiState = uiState.value,
            navigateToMap = navigateToMap,
            viewModel = viewModel,
            classRoomList = room,
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding()
                )
        )

    }
}

@Composable
private fun BuildingDetailsBody(
    mapType: OSMCustomMapType,
    viewModel: BuildingDetailsViewModel,
    navigateToMap: (Int) -> Unit,
    navigateToRoomDetails: (Int) -> Unit,
    buildingDetailsUiState: BuildingDetailsUiState,
    classRoomList: List<Classroom>,
    modifier: Modifier = Modifier
){
    val coroutineScope = rememberCoroutineScope()
    val building = buildingDetailsUiState.buildingDetails.toBuilding()
    val colorEntry = rememberUpdatedState(CollegeColorPalette.getColorEntry(building.college))
    val fontColor = colorEntry.value.fontColor
    val backgroundColor = colorEntry.value.backgroundColor
    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ){
        if(building.college == "Landmark" || building.college == "Dormitory" || building.college == "UP unit"){
            LocationDetail(
                mapType = mapType,
                building = building,
                viewModel = viewModel,
                fontColor = fontColor,
                backgroundColor = backgroundColor,
                navigateToMap = navigateToMap,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        else {
            BuildingDetail(
                building = building,
                fontColor = fontColor,
                backgroundColor = backgroundColor,
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                onClick = {
                    coroutineScope.launch{ viewModel.addOrUpdateMapData(building.toBuildingDetails())}
                    navigateToMap(0)
                    },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.guide))
            }
            Text(text = stringResource(R.string.rooms), fontWeight = FontWeight.Bold)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(classRoomList) { classroom ->
                    RoomDetails(
                        classroom = classroom,
                        fontColor = fontColor,
                        backgroundColor = backgroundColor,
                        modifier = Modifier
                            .padding(dimensionResource(id = R.dimen.padding_small))
                            .clickable { navigateToRoomDetails(classroom.roomId) }
                    )
                }
            }
        }
    }
}


@Composable
fun LocationDetail(
    building: Building,
    modifier: Modifier = Modifier,
    viewModel: BuildingDetailsViewModel,
    fontColor: Color,
    backgroundColor: Color,
    navigateToMap: (Int) -> Unit,
    mapType: OSMCustomMapType
){
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.padding_medium)
            )
        ) {
            BuildingDetailsRow(
                labelResID = R.string.name,
                fontColor = fontColor,
                buildingDetail = building.name,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                )
            )
            BuildingDetailsRow(
                labelResID = R.string.type,
                buildingDetail = building.college,
                fontColor = fontColor,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                )
            )
        }
    }
    Text(text = stringResource(R.string.location), fontWeight = FontWeight.Bold)
    Box(
        modifier = Modifier
            .height(300.dp)
            .fillMaxWidth()
    ) {
        OSMMapping(
            title = building.name,
            latitude = building.latitude,
            longitude = building.longitude,
            styleUrl = mapType.styleUrl
        )
    }

    Button(
        onClick = {
            coroutineScope.launch{ viewModel.addOrUpdateMapData(building.toBuildingDetails())}
            navigateToMap(0)},//Navigate to GuideMap Screen
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = stringResource(R.string.guide))
    }
}


@Composable
fun BuildingDetail(
    building: Building,
    fontColor: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
){
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.padding_medium)
            )
        ){
            BuildingDetailsRow(
                labelResID = R.string.abbreviation,
                fontColor = fontColor,
                buildingDetail = building.abbreviation,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                )
            )
            BuildingDetailsRow(
                labelResID = R.string.college,
                fontColor = fontColor,
                buildingDetail = building.college,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                )
            )
        }
    }
}

@Composable
private fun BuildingDetailsRow(
    @StringRes labelResID: Int,
    fontColor: Color,
    buildingDetail: String,
    modifier: Modifier = Modifier,
){
    Row(modifier = modifier){
        Text(text = stringResource(id = labelResID), color = fontColor)
        Spacer(modifier = Modifier.weight(1f))
        Text(text = buildingDetail, fontWeight = FontWeight.Bold, color = fontColor)
    }
}

@Composable
private fun RoomDetails(
    classroom: Classroom,
    fontColor: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            Text(
                text = classroom.abbreviation,
                style = MaterialTheme.typography.bodyMedium,
                color = fontColor
            )
        }
    }
}