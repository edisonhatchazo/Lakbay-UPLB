package com.example.classschedule.ui.classes

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.classschedule.R
import com.example.classschedule.data.ClassSchedule
import com.example.classschedule.ui.navigation.AppViewModelProvider
import com.example.classschedule.ui.navigation.NavigationDestination
import com.example.classschedule.ui.screen.DetailsScreenTopAppBar
import com.example.classschedule.ui.theme.ColorEntry
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter


object ClassScheduleDetailsDestination : NavigationDestination {
    override val route = "class_schedule_details"
    override val titleRes = R.string.class_detail_title
    const val CLASSSCHEDULEIDARG = "ClassScheduleId"
    val routeWithArgs = "$route/{$CLASSSCHEDULEIDARG}"
}

@Composable
fun ClassScheduleDetailsScreen (
    navigateToEditClassSchedule: (Int) -> Unit,
    navigateBack: () -> Unit,
    navigateToMap: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ClassScheduleDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    val uiState = viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            DetailsScreenTopAppBar(
                title = stringResource(ClassScheduleDetailsDestination.titleRes),
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        }, floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateToEditClassSchedule(uiState.value.classScheduleDetails.id) },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit_class_title)
                )
            }
        }, modifier = modifier
    ) { innerPadding ->
        ClassScheduleDetailsBody(
            classScheduleDetailsUiState = uiState.value,
            navigateToMap = navigateToMap,
            viewModel = viewModel,
            onDelete = {
                coroutineScope.launch {
                    viewModel.deleteClassSchedule()
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
private fun ClassScheduleDetailsBody(
    classScheduleDetailsUiState: ClassScheduleDetailsUiState,
    onDelete: () -> Unit,
    navigateToMap: (Int) -> Unit,
    viewModel: ClassScheduleDetailsViewModel,
    modifier: Modifier = Modifier
){
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }
        ClassScheduleDetails(
            classSchedule = classScheduleDetailsUiState.classScheduleDetails.toClass(),
            viewModel = viewModel,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                coroutineScope.launch{ viewModel.addOrUpdateMapData(classScheduleDetailsUiState)}
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
fun ClassScheduleDetails(
    classSchedule: ClassSchedule, modifier: Modifier = Modifier, viewModel: ClassScheduleDetailsViewModel
) {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    val colorSchemes by viewModel.colorSchemes.collectAsState()

    // Get the appropriate color entry for the class schedule
    val colorEntry = colorSchemes[classSchedule.colorId] ?: ColorEntry(Color.Transparent, Color.Black)

    val fontColor = colorEntry.fontColor
    val backgroundColor = colorEntry.backgroundColor
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = fontColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.padding_medium)
            )
        ) {
            ClassDetailsRow(
                labelResID = R.string.classes,
                classScheduleDetail = classSchedule.title,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                )
            )
            ClassDetailsRow(
                labelResID = R.string.teacher,
                classScheduleDetail = classSchedule.teacher,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                )
            )
            ClassDetailsRow(
                labelResID = R.string.location,
                classScheduleDetail = classSchedule.location,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                )
            )
            ClassDetailsRow(
                labelResID = R.string.day,
                classScheduleDetail = classSchedule.days,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                )
            )
            ClassDetailsRow(
                labelResID = R.string.time_start,
                classScheduleDetail = classSchedule.time.format(timeFormatter),
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                )
            )
            ClassDetailsRow(
                labelResID = R.string.time_end,
                classScheduleDetail = classSchedule.timeEnd.format(timeFormatter),
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                )
            )
        }
    }
}

@Composable
private fun ClassDetailsRow(
    @StringRes labelResID: Int, classScheduleDetail: String, modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(stringResource(labelResID))
        Spacer(modifier = Modifier.weight(1f))
        Text(text = classScheduleDetail, fontWeight = FontWeight.Bold)
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