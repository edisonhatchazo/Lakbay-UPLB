package com.edison.lakbayuplb.ui.settings.colors

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
import com.edison.lakbayuplb.ui.screen.ColorScreenDetailTopAppBar
import com.edison.lakbayuplb.ui.settings.global.TopAppBarColorSchemesViewModel
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
    viewModel: ColorSchemeEditViewModel = viewModel(factory = AppViewModelProvider.Factory),
    colorViewModel: TopAppBarColorSchemesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val topAppBarColors = colorViewModel.topAppBarColors.collectAsState()
    val (topAppBarBackgroundColor, topAppBarForegroundColor) = topAppBarColors.value

    val colorSchemesUiState = viewModel.colorSchemesUiState
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            ColorScreenDetailTopAppBar(
                title = stringResource(ColorSchemeEditDestination.titleRes),
                canNavigateBack = canNavigateBack,
                onNavigateUp = onNavigateUp,
                topAppBarBackgroundColor = topAppBarBackgroundColor,
                topAppBarForegroundColor = topAppBarForegroundColor
            )
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
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                )
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        )
    }
}
