package com.edison.lakbayuplb.ui.buildingScreens.uplb.buildings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edison.lakbayuplb.R
import com.edison.lakbayuplb.data.building.Building
import com.edison.lakbayuplb.ui.navigation.AppViewModelProvider
import com.edison.lakbayuplb.ui.navigation.NavigationDestination
import com.edison.lakbayuplb.ui.screen.BuildingsScreenTopAppBar
import com.edison.lakbayuplb.ui.settings.global.TopAppBarColorSchemesViewModel
import com.edison.lakbayuplb.ui.theme.CollegeColorPalette

object BuildingHomeDestination: NavigationDestination {
    override val route = "building_home"
    override val titleRes = R.string.buildings_title
}

@Composable
fun BuildingHomeScreen(
    modifier: Modifier = Modifier,
    openDrawer: () -> Unit,
    navigateToRoomDetails: (Int) -> Unit,
    navigateToBuildingEntry: () -> Unit,
    navigateToAboutPage: () -> Unit,
    navigateToBuildingDetails: (Int) -> Unit,
    viewModel: BuildingHomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    colorViewModel: TopAppBarColorSchemesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val topAppBarColors = colorViewModel.topAppBarColors.collectAsState()
    val (topAppBarBackgroundColor, topAppBarForegroundColor) = topAppBarColors.value

    val homeUiState by viewModel.buildingHomeUiState.collectAsState()
    val categories = listOf(
        "CAS" to "College of Arts and Sciences",
        "CDC" to "College of Development Communication",
        "CA" to "College of Agriculture",
        "CVM" to "College of Veterinary Medicine",
        "CHE" to "College of Human Ecology",
        "CPAf" to "College of Public Affairs and Development",
        "CFNR" to "College of Forestry and Natural Resources",
        "CEM" to "College of Economics and Management",
        "CEAT" to "College of Engineering and Agro-industrial Technology",
        "GS" to "Graduate School",
        "UP Unit" to "UP Unit",
        "Dormitory" to "Dormitory",
        "Landmark" to "Landmark"
    )
    var selectedCategory by remember { mutableStateOf(categories.first().second) }



    Scaffold(
        modifier = modifier,
        topBar = {
            BuildingsScreenTopAppBar(
                title = stringResource(BuildingHomeDestination.titleRes),
                canNavigateBack = false,
                openDrawer = openDrawer,
                navigateToAboutPage = navigateToAboutPage,
                navigateToBuildingEntry = navigateToBuildingEntry,
                navigateToRoomDetails = navigateToRoomDetails,
                navigateToBuildingDetails = navigateToBuildingDetails,
                topAppBarForegroundColor = topAppBarForegroundColor,
                topAppBarBackgroundColor = topAppBarBackgroundColor
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            CategoryTabs(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )
            BuildingHomeBody(
                selectedCategory = selectedCategory,
                buildingList = homeUiState.buildingList.filter { it.college == selectedCategory },
                modifier = Modifier.padding(top = 8.dp),
                onBuildingClick = navigateToBuildingDetails
            )
        }
    }
}

@Composable
fun BuildingHomeBody(
    selectedCategory: String,
    buildingList: List<Building>,
    modifier: Modifier = Modifier,
    onBuildingClick: (Int) -> Unit,
){
    //val filteredBuildingList = buildingList.filterNot { it.college == "Landmark" || it.college == "Dormitory" || it.college == "UP Unit"}
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ){
       BuildingList(
           selectedCategory = selectedCategory,
           buildingList = buildingList,
           onBuildingClick = {onBuildingClick(it.buildingId)},
           modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))
       )
    }
}

@Composable
private fun BuildingList(
    selectedCategory: String,
    buildingList: List<Building>,
    onBuildingClick: (Building) -> Unit,
    modifier: Modifier = Modifier
){
    LazyColumn(modifier = modifier) {
        items(items = buildingList,key = {it.buildingId}){building ->
            BuildingDetails(
                selectedCategory = selectedCategory,
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
    selectedCategory: String,
    building: Building,
    modifier: Modifier = Modifier,

    ){

    val colorEntry = rememberUpdatedState(CollegeColorPalette.getColorEntry(selectedCategory))

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = colorEntry.value.backgroundColor)
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
                    color = colorEntry.value.fontColor
                )
            }
        }
    }
}

@Composable
fun CategoryTabs(
    categories: List<Pair<String, String>>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val selectedIndex = categories.indexOfFirst { it.second == selectedCategory }

    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        edgePadding = 8.dp,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                height = 4.dp,
                color = MaterialTheme.colorScheme.primary
            )
        },
        modifier = Modifier.height(48.dp) // Increase height for better visibility
    ) {
        categories.forEachIndexed { index, category ->
            Tab(
                selected = selectedIndex == index,
                onClick = { onCategorySelected(category.second) },
                text = {
                    Text(
                        text = category.first,
                        modifier = Modifier.padding(horizontal = 8.dp), // Add padding around text
                        style = MaterialTheme.typography.bodyLarge.copy(color = if (selectedIndex == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                    )
                }
            )
        }
    }
}