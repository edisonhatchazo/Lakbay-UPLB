package com.edison.lakbayuplb.ui.settings.colors

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edison.lakbayuplb.R
import com.edison.lakbayuplb.ui.navigation.AppViewModelProvider
import com.edison.lakbayuplb.ui.navigation.NavigationDestination
import com.edison.lakbayuplb.ui.screen.ColorScreenDetailTopAppBar
import com.edison.lakbayuplb.ui.settings.global.TopAppBarColorSchemesViewModel
import com.edison.lakbayuplb.ui.theme.Typography
import kotlinx.coroutines.launch

object ColorSchemeDetailsDestination : NavigationDestination {
    override val route = "colors_details"
    override val titleRes = R.string.color_detail_title
    const val COLORIDARG = "id"
    val routeWithArgs = "$route/{$COLORIDARG}"
}

@Composable
fun ColorSchemeDetails(
    navigateBack: () -> Unit,
    navigateToEditColorScheme: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ColorSchemeDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    colorViewModel: TopAppBarColorSchemesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val topAppBarColors = colorViewModel.topAppBarColors.collectAsState()
    val (topAppBarBackgroundColor, topAppBarForegroundColor) = topAppBarColors.value

    val uiState = viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            ColorScreenDetailTopAppBar(
                title = stringResource(ColorSchemeDetailsDestination.titleRes),
                canNavigateBack = true,
                onNavigateUp = navigateBack,
                topAppBarForegroundColor = topAppBarForegroundColor,
                topAppBarBackgroundColor = topAppBarBackgroundColor
            )
        }, modifier = modifier,
    ){innerPadding ->
        ColorSchemeDetailsBody(
            colorDetails = uiState.value.colorSchemeDetails,
            onDelete = {
                coroutineScope.launch {
                    viewModel.deleteColorScheme()
                    navigateBack()
                }
            },
            navigateToEditColorScheme = navigateToEditColorScheme,
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                )
                .verticalScroll(rememberScrollState())
        )


    }
}

@Composable
fun ColorSchemeDetailsBody(
    colorDetails: ColorSchemeDetails,
    navigateToEditColorScheme: (Int) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier
){
    var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }
    val enabled = colorDetails.isCurrentlyUsed == 0

    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ){
        Text(
            text = stringResource(id = R.string.edit_color),
            style = Typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = stringResource(id = R.string.cannot_be_deleted),
            style = Typography.bodySmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        ColorSchemeDetailed(colorDetails = colorDetails, modifier = Modifier.fillMaxWidth())
        Button(
            onClick = {navigateToEditColorScheme(colorDetails.id) },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.edit_color))
        }
        OutlinedButton(
            onClick = { deleteConfirmationRequired = true },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled
        ) {
            Text(stringResource(R.string.delete))
        }
        if (deleteConfirmationRequired) {
            DeleteConfirmationDialog(
                onDeleteConfirm = {
                    deleteConfirmationRequired = false
                    onDelete()
                },
                onDeleteCancel = { deleteConfirmationRequired = false },
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
            )
        }
    }
}

@Composable
fun ColorSchemeDetailed(
    colorDetails: ColorSchemeDetails,
    modifier: Modifier
){

    val fontColor = Color(colorDetails.fontColor)
    val backgroundColor = Color(colorDetails.backgroundColor)
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = fontColor
        )
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.padding_medium)
            )
        ) {
            Box(
                modifier = Modifier
                    .background(backgroundColor)
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(colorDetails.name, color = fontColor)
            }
        }
    }

}


@Composable
private fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = { /*Do Nothing*/ },
        title = { Text(stringResource(R.string.delete_question)) },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text(stringResource(R.string.no))
            }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Text(stringResource(R.string.yes))
            }
        }
    )
}
