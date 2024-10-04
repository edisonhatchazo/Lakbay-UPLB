package com.edison.lakbayuplb.ui.buildingScreens.pins

import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edison.lakbayuplb.R
import com.edison.lakbayuplb.ui.map.OSMCustomMapType
import com.edison.lakbayuplb.ui.navigation.AppViewModelProvider
import com.edison.lakbayuplb.ui.navigation.NavigationDestination
import com.edison.lakbayuplb.ui.screen.CoordinateEntryScreenTopAppBar
import kotlinx.coroutines.launch

object PinsEditDestination: NavigationDestination {
    override val route = "pins_edit"
    override val titleRes = R.string.edit_pin_title
    const val PINSIDARG = "pinId"
    val routeWithArgs = "$route/{$PINSIDARG}"
}

@Composable
fun PinsEditScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PinsEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val pinsUiState = viewModel.pinsUiState
    val mapType by remember { mutableStateOf(OSMCustomMapType.OSM_3D) }
    Scaffold(
        topBar = {
            CoordinateEntryScreenTopAppBar(
                title = stringResource(PinsEditDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onNavigateUp,
            )
        },
        modifier = modifier
    ){ innerPadding ->
        PinsEntryBody(
            pinsUiState = pinsUiState,
            onPinsValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch{
                    viewModel.updatePin()
                    navigateBack()
                }
            },
            mapType = mapType,
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
