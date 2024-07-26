package com.example.classschedule.ui.buildingScreens.pins

import android.util.Log
import androidx.annotation.StringRes
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.classschedule.R
import com.example.classschedule.data.Pins
import com.example.classschedule.ui.map.OSMCustomMapType
import com.example.classschedule.ui.map.OSMDetailsMapping
import com.example.classschedule.ui.navigation.AppViewModelProvider
import com.example.classschedule.ui.navigation.NavigationDestination
import com.example.classschedule.ui.screen.CoordinateEntryScreenTopAppBar
import kotlinx.coroutines.launch

object PinsDetailsDestination : NavigationDestination {
    override val route = "pins_details"
    override val titleRes = R.string.pin_detail_title
    const val PINIDARG = "PinId"
    val routeWithArgs = "$route/{$PINIDARG}"
}
@Composable
fun PinsDetailsScreen(
    navigateToEditPin: (Int) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    navigateToMap: (Int) -> Unit,
    mainNavController: NavHostController,
    viewModel: PinsDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var mapType by remember { mutableStateOf(OSMCustomMapType.STREET) }

    Scaffold(
        topBar = {
            CoordinateEntryScreenTopAppBar(
                title = stringResource(PinsDetailsDestination.titleRes),
                canNavigateBack = true,
                navigateUp = navigateBack,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateToEditPin(uiState.pinsDetails.id) },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit_pin_title)
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        PinsDetailsBody(
            pinsDetailsUiState = uiState,
            onDelete = {
                coroutineScope.launch {
                    viewModel.deletePin()
                    navigateBack()
                }
            },
            navigateToMap = navigateToMap,
            viewModel = viewModel,
            mapType = mapType,
            navController = mainNavController,
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
private fun PinsDetailsBody(
    pinsDetailsUiState: PinsDetailsUiState,
    onDelete: () -> Unit,
    navigateToMap: (Int) -> Unit,
    viewModel: PinsDetailsViewModel,
    navController: NavHostController,
    mapType: OSMCustomMapType,
    modifier: Modifier = Modifier
){
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        Log.d("MapScreen","From Pins")
        var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }
        val pin = pinsDetailsUiState.pinsDetails.toPins()
        PinDetails(
            pin = pin,
            modifier = Modifier.fillMaxWidth(),
            mapType = mapType
        )
        Button(
            onClick = {
                coroutineScope.launch{ viewModel.addOrUpdateMapData(pin.toPinsDetails())}
                navigateToMap(0)
            },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.guide))
        }
        OutlinedButton(
            onClick = { deleteConfirmationRequired = true },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
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
fun PinDetails(
    pin: Pins, modifier: Modifier = Modifier, mapType: OSMCustomMapType,
) {

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
            PinDetailsRow(
                labelResID = R.string.my_pins,
                pinDetail = pin.title,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                )
            )
            PinDetailsRow(
                labelResID = R.string.floor,
                pinDetail = pin.floor,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                )
            )

        }
    }
    Box(
        modifier = Modifier
            .height(300.dp)
            .fillMaxWidth()
    ) {
        OSMDetailsMapping(
            title = pin.title,
            latitude = pin.latitude,
            longitude = pin.longitude,
            styleUrl = mapType.styleUrl
        )
    }
}
@Composable
private fun PinDetailsRow(
    @StringRes labelResID: Int, pinDetail: String, modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(stringResource(labelResID))
        Spacer(modifier = Modifier.weight(1f))
        Text(text = pinDetail, fontWeight = FontWeight.Bold)
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