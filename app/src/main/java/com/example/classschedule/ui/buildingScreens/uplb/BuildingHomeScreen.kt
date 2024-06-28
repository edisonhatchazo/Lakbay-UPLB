package com.example.classschedule.ui.buildingScreens.uplb

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.classschedule.R
import com.example.classschedule.data.Building
import com.example.classschedule.ui.navigation.AppViewModelProvider
import com.example.classschedule.ui.navigation.NavigationDestination
import com.example.classschedule.ui.screen.BuildingsScreenTopAppBar
import com.example.classschedule.ui.theme.CollegeColorPalette

object BuildingHomeDestination: NavigationDestination {
    override val route = "building_home"
    override val titleRes = R.string.buildings_title
}

@Composable
fun BuildingHomeScreen(
    modifier: Modifier = Modifier,
    navigateToPinsHomeDestination: () -> Unit,
    navigateToRoomDetails: (Int) -> Unit,
    navigateToBuildingDetails: (Int) -> Unit,
    viewModel: BuildingHomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.buildingHomeUiState.collectAsState()
    Scaffold(
        modifier = modifier,
        topBar = {
            BuildingsScreenTopAppBar(
                title = stringResource(BuildingHomeDestination.titleRes),
                canNavigateBack = false,
                navigateToPinsHome = navigateToPinsHomeDestination,
                navigateToRoomDetails = navigateToRoomDetails,
                navigateToBuildingDetails = navigateToBuildingDetails
            )
        }
    ){innerPadding->
        BuildingHomeBody(
            buildingList = homeUiState.buildingList,
            modifier = modifier.padding(innerPadding),
            onBuildingClick = navigateToBuildingDetails,
        )
    }
}

@Composable
fun BuildingHomeBody(
    buildingList: List<Building>,
    modifier: Modifier = Modifier,
    onBuildingClick: (Int) -> Unit,
){
    val filteredBuildingList = buildingList.filterNot { it.college == "Landmark" || it.college == "Dormitory" || it.college == "UP Unit"}
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ){
       BuildingList(
           buildingList = filteredBuildingList,
           onBuildingClick = {onBuildingClick(it.buildingId)},
           modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))
       )
    }
}

@Composable
private fun BuildingList(
    buildingList: List<Building>,
    onBuildingClick: (Building) -> Unit,
    modifier: Modifier = Modifier
){
    LazyColumn(modifier = modifier) {
        items(items = buildingList,key = {it.buildingId}){building ->
            BuildingDetails(
                building = building,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
                    .clickable { onBuildingClick(building) }
            )
        }
    }
}


@Composable
private fun BuildingDetails(
    building: Building,
    modifier: Modifier = Modifier
){
    val colorEntry = CollegeColorPalette.getColorEntry(building.college)
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = colorEntry.backgroundColor)
    ){
        Column(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ){
                Text(
                    text = building.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = colorEntry.fontColor
                )
            }
        }
    }
}