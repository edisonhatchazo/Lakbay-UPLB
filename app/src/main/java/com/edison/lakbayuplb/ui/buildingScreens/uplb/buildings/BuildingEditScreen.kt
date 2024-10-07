package com.edison.lakbayuplb.ui.buildingScreens.uplb.buildings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edison.lakbayuplb.R
import com.edison.lakbayuplb.ui.navigation.AppViewModelProvider
import com.edison.lakbayuplb.ui.navigation.NavigationDestination
import com.edison.lakbayuplb.ui.screen.EntryScreenTopAppBar
import com.edison.lakbayuplb.ui.settings.global.TopAppBarColorSchemesViewModel
import kotlinx.coroutines.launch

object BuildingEditDestination: NavigationDestination {
    override val route = "building_edit"
    override val titleRes = R.string.building_edit_title
    const val BUILDINGIDARG = "buildingId"
    val routeWithArgs = "$route/{$BUILDINGIDARG}"
}
@Composable
fun BuildingEditScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BuildingEditViewModel = viewModel(factory = AppViewModelProvider.Factory),
    colorViewModel: TopAppBarColorSchemesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val topAppBarColors = colorViewModel.topAppBarColors.collectAsState()
    val (topAppBarBackgroundColor, topAppBarForegroundColor) = topAppBarColors.value

    val coroutineScope = rememberCoroutineScope()
    val buildingUiState = viewModel.buildingUiState
    Scaffold(
        topBar = {
            EntryScreenTopAppBar(
                title = stringResource(BuildingEditDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onNavigateUp,
                topAppBarBackgroundColor = topAppBarBackgroundColor, // Pass the observed background color
                topAppBarForegroundColor = topAppBarForegroundColor  // Pass the observed foreground color

            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
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
                        viewModel.updateBuilding()
                        navigateBack()
                    }
                },
                onBuildingValueChange = viewModel::updateUiState,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
