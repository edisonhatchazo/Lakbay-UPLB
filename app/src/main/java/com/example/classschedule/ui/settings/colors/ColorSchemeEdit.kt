package com.example.classschedule.ui.settings.colors

import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.classschedule.R
import com.example.classschedule.ui.navigation.AppViewModelProvider
import com.example.classschedule.ui.navigation.NavigationDestination
import com.example.classschedule.ui.screen.ColorScreenDetailTopAppBar
import kotlinx.coroutines.launch

object ColorSchemeEditDestination: NavigationDestination {
    override val route = "colors_edit"
    override val titleRes = R.string.edit_color
    const val COLORIDARG = "id"
    val routeWithArgs = "$route/{$COLORIDARG}"
}

@Composable
fun ColorSchemeEdit(
    onNavigateUp: () -> Unit,
    navigateBack: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: ColorSchemeEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val colorSchemesUiState = viewModel.colorSchemesUiState
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            ColorScreenDetailTopAppBar(
                title = stringResource(ColorSchemeEditDestination.titleRes),
                canNavigateBack = canNavigateBack,
                onNavigateUp = onNavigateUp)
        }
    ) { innerPadding ->
        ColorPickingScreen(
            colorSchemeDetails = colorSchemesUiState.colorSchemeDetails,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.updateColor()
                    navigateBack()
                }
            },
            onColorValueChange = viewModel::updateUiState,
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding()
                )
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        )
    }
}
