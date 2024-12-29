package com.edison.lakbayuplb.ui.buildingScreens.uplb.rooms

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edison.lakbayuplb.R
import com.edison.lakbayuplb.algorithm.routing_algorithm.checkCurrentLocation
import com.edison.lakbayuplb.data.building.Classroom
import com.edison.lakbayuplb.ui.buildingScreens.uplb.toClassroom
import com.edison.lakbayuplb.ui.buildingScreens.uplb.toClassroomDetails
import com.edison.lakbayuplb.ui.navigation.AppViewModelProvider
import com.edison.lakbayuplb.ui.navigation.NavigationDestination
import com.edison.lakbayuplb.ui.screen.EditScreenTopAppBar
import com.edison.lakbayuplb.ui.settings.global.TopAppBarColorSchemesViewModel
import com.edison.lakbayuplb.ui.theme.CollegeColorPalette
import kotlinx.coroutines.launch

object RoomDetailsDestination : NavigationDestination {
    override val route = "room_details"
    override val titleRes = R.string.room_detail_title
    const val ROOMIDARG = "roomId"
    val routeWithArgs = "$route/{$ROOMIDARG}"
}

@Composable
fun RoomDetailsScreen(
    navigateBack: () -> Unit,
    navigateToMap: (Int) -> Unit,
    modifier: Modifier = Modifier,
    navigateToClassroomEdit: (Int) -> Unit,
    viewModel: RoomDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    colorViewModel: TopAppBarColorSchemesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val topAppBarColors = colorViewModel.topAppBarColors.collectAsState()
    val (topAppBarBackgroundColor, topAppBarForegroundColor) = topAppBarColors.value

    val coroutineScope = rememberCoroutineScope()
    val roomUiState = viewModel.uiState.collectAsState()
    val room = roomUiState.value.classroomDetails.toClassroom()

    Scaffold(
        topBar = {
            EditScreenTopAppBar(
                title = room.abbreviation,
                canNavigateBack = true,
                navigateUp = navigateBack,
                id = room.roomId,
                navigateToEdit = navigateToClassroomEdit,
                topAppBarBackgroundColor = topAppBarBackgroundColor,
                topAppBarForegroundColor = topAppBarForegroundColor
            )
        }, modifier = modifier
    ){innerPadding ->
        ClassroomDetailsBody(
            navigateToMap = navigateToMap,
            classroomDetailsUiState = roomUiState.value,
            viewModel = viewModel,
            buildingCollege = room.college,
            onDelete = {
                coroutineScope.launch {
                    viewModel.deleteClassroom()
                    navigateBack()
                }
            },
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
private fun ClassroomDetailsBody(
    classroomDetailsUiState: ClassroomDetailsUiState,
    buildingCollege:String,
    navigateToMap: (Int) -> Unit,
    viewModel: RoomDetailsViewModel,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
){
    val classroom = classroomDetailsUiState.classroomDetails.toClassroom()

    val colorEntry = rememberUpdatedState(CollegeColorPalette.getColorEntry(buildingCollege))
    val fontColor = colorEntry.value.fontColor
    val backgroundColor = colorEntry.value.backgroundColor
    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        ClassroomDetailed(
            fontColor = fontColor,
            backgroundColor = backgroundColor,
            classroom = classroom,
            viewModel = viewModel,
            navigateToMap = navigateToMap,
            modifier = Modifier.fillMaxWidth(),
            onDelete = onDelete
        )

    }
}

@Composable
fun ClassroomDetailed(
    classroom: Classroom,
    modifier: Modifier = Modifier,
    navigateToMap: (Int) -> Unit,
    onDelete: () -> Unit,
    viewModel: RoomDetailsViewModel,
    fontColor: Color,
    backgroundColor: Color
){
    val coroutineScope = rememberCoroutineScope()
    var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    var showOutOfBoundsDialog by rememberSaveable { mutableStateOf(false) }
    val isInsideBounds = checkCurrentLocation(context)
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
            ClassroomDetailsRow(
                labelResID = R.string.title,
                fontColor = fontColor,
                classroomDetail = classroom.title,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                )
            )
            ClassroomDetailsRow(
                labelResID = R.string.floor,
                fontColor = fontColor,
                classroomDetail = classroom.floor,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                )
            )
        }
    }
    Button(
        onClick = {
            if(isInsideBounds) {
                coroutineScope.launch { viewModel.addOrUpdateMapData(classroom.toClassroomDetails()) }
                navigateToMap(0)
            }else{
                showOutOfBoundsDialog = true
            }
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
    if (showOutOfBoundsDialog) {
        AlertDialog(
            onDismissRequest = { showOutOfBoundsDialog = false },
            title = { Text("Location Out of Bounds") },
            text = { Text("Your current location is outside the University of the Philippines Los Baños campus.") },
            confirmButton = {
                TextButton(onClick = { showOutOfBoundsDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
private fun ClassroomDetailsRow(
    @StringRes labelResID: Int,
    classroomDetail: String,
    fontColor: Color,
    modifier: Modifier = Modifier,
){
    Row(modifier = modifier) {
        Text(
            text = stringResource(labelResID),
            color = fontColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(0.3f),
            maxLines = 4
        )

        Text(
            text = classroomDetail,
            modifier = Modifier.weight(0.8f),
            color = fontColor,
            maxLines = 4
        )
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