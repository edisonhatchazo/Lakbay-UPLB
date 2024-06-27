package com.example.classschedule.ui.buildingScreens.uplb

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.classschedule.R
import com.example.classschedule.data.Building
import com.example.classschedule.data.Classroom
import com.example.classschedule.ui.navigation.AppViewModelProvider
import com.example.classschedule.ui.navigation.NavigationDestination
import com.example.classschedule.ui.screen.DetailsScreenTopAppBar
import com.example.classschedule.ui.theme.CollegeColorPalette
import com.example.classschedule.ui.theme.ColorEntry

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
    viewModel: BuildingDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    val uiState = viewModel.uiState.collectAsState()
    val roomUiState by viewModel.buildingRoomUiState.collectAsState()
    val room = roomUiState.roomList
    val build = uiState.value.buildingDetails.toBuilding()

    Scaffold(
        topBar = {
            DetailsScreenTopAppBar(
                title = build.name,
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        }, modifier = modifier
    ){innerPadding ->
        BuildingDetailsBody(
            navigateToRoomDetails = navigateToRoomDetails,
            buildingDetailsUiState = uiState.value,
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
    navigateToRoomDetails: (Int) -> Unit,
    buildingDetailsUiState: BuildingDetailsUiState,
    classRoomList: List<Classroom>,
    modifier: Modifier = Modifier
){
    val building = buildingDetailsUiState.buildingDetails.toBuilding()
    val colorEntry = CollegeColorPalette.getColorEntry(building.college)

    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ){
        BuildingDetail(
            building = building,
            modifier = Modifier.fillMaxWidth(),
            colorEntry = colorEntry
        )
        Text(text = stringResource(R.string.rooms), fontWeight = FontWeight.Bold)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(classRoomList){classroom ->
                RoomDetails(
                    classroom = classroom,
                    colorEntry = colorEntry,
                    modifier = Modifier
                        .padding(dimensionResource(id =R.dimen.padding_small ))
                        .clickable{navigateToRoomDetails(classroom.roomId)}
                )
            }
        }
    }
}

@Composable
private fun RoomDetails(
    colorEntry: ColorEntry,
    classroom: Classroom,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = colorEntry.backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            Text(
                text = classroom.abbreviation,
                style = MaterialTheme.typography.bodyMedium,
                color = colorEntry.fontColor
            )

        }
    }
}

@Composable
fun BuildingDetail(
    building: Building,
    modifier: Modifier = Modifier,
    colorEntry: ColorEntry
){
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = colorEntry.backgroundColor)
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
                colorEntry = colorEntry,
                buildingDetail = building.abbreviation,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                )
            )
            BuildingDetailsRow(
                labelResID = R.string.college,
                colorEntry = colorEntry,
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
    buildingDetail: String,
    modifier: Modifier = Modifier,
    colorEntry: ColorEntry
){
    Row(modifier = modifier){
        Text(text = stringResource(id = labelResID), color = colorEntry.fontColor)
        Spacer(modifier = Modifier.weight(1f))
        Text(text = buildingDetail, fontWeight = FontWeight.Bold, color = colorEntry.fontColor)
    }
}
