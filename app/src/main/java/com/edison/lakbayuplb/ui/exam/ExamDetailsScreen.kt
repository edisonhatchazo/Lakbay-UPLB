package com.edison.lakbayuplb.ui.exam

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edison.lakbayuplb.R
import com.edison.lakbayuplb.algorithm.routing_algorithm.checkCurrentLocation
import com.edison.lakbayuplb.data.classes.ExamSchedule
import com.edison.lakbayuplb.ui.navigation.AppViewModelProvider
import com.edison.lakbayuplb.ui.navigation.NavigationDestination
import com.edison.lakbayuplb.ui.screen.DetailsScreenTopAppBar
import com.edison.lakbayuplb.ui.settings.global.TopAppBarColorSchemesViewModel
import com.edison.lakbayuplb.ui.theme.ColorEntry
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

object ExamDetailsDestination : NavigationDestination {
    override val route = "exam_schedule_details"
    override val titleRes = R.string.exam_detail_title
    const val SCHEDULEIDARG = "scheduleId"
    val routeWithArgs = "$route/{$SCHEDULEIDARG}"
}

@Composable
fun ExamDetailsScreen(
    navigateToEditExam: (Int) -> Unit,
    navigateBack: () -> Unit,
    navigateToAboutPage: () -> Unit,
    modifier: Modifier = Modifier,
    navigateToMap: (Int) -> Unit,
    viewModel: ExamDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    colorViewModel: TopAppBarColorSchemesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val topAppBarColors = colorViewModel.topAppBarColors.collectAsState()
    val (topAppBarBackgroundColor, topAppBarForegroundColor) = topAppBarColors.value

    val uiState = viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            DetailsScreenTopAppBar(
                title = stringResource(ExamDetailsDestination.titleRes),
                canNavigateBack = true,
                navigateUp = navigateBack,
                navigateToAboutPage = navigateToAboutPage,
                topAppBarForegroundColor = topAppBarForegroundColor,
                topAppBarBackgroundColor = topAppBarBackgroundColor
            )
        }, floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateToEditExam(uiState.value.examScheduleDetails.id) },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit_exam_title)
                )
            }
        }, modifier = modifier
    ) { innerPadding ->
        ExamScheduleDetailsBody(
            examScheduleDetailsUiState = uiState.value,
            navigateToMap = navigateToMap,
            viewModel = viewModel,
            onDelete = {
                coroutineScope.launch {
                    viewModel.deleteExamSchedule()
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
private fun ExamScheduleDetailsBody(
    examScheduleDetailsUiState: ExamScheduleDetailsUiState,
    navigateToMap: (Int) -> Unit,
    viewModel: ExamDetailsViewModel,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var showOutOfBoundsDialog by rememberSaveable { mutableStateOf(false) }
    val isInsideBounds = checkCurrentLocation(context)

    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }

        ExamScheduleDetails(
            examSchedule = examScheduleDetailsUiState.examScheduleDetails.toExam(),
            modifier = Modifier.fillMaxWidth(),
            viewModel = viewModel
        )

        Button(
            onClick = {
                if (isInsideBounds) {
                    coroutineScope.launch{ viewModel.addOrUpdateMapData(examScheduleDetailsUiState)}
                    navigateToMap(0)
                } else {
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
fun ExamScheduleDetails(
    examSchedule: ExamSchedule, modifier: Modifier = Modifier, viewModel: ExamDetailsViewModel
) {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    val colorSchemes by viewModel.colorSchemes.collectAsState()

    // Get the appropriate color entry for the class schedule
    val colorEntry = colorSchemes[examSchedule.colorId] ?: ColorEntry(Color.Transparent, Color.Black)

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
            ExamsDetailsRow(
                labelResID = R.string.course_number,
                examScheduleDetail = examSchedule.title,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                )
            )
            ExamsDetailsRow(
                labelResID = R.string.section,
                examScheduleDetail = examSchedule.section,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                )
            )
            ExamsDetailsRow(
                labelResID = R.string.teacher,
                examScheduleDetail = examSchedule.teacher,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                )
            )
            ExamsDetailsRow(
                labelResID = R.string.location,
                examScheduleDetail = examSchedule.location,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                )
            )
            ExamsDetailsRow(
                labelResID = R.string.date,
                examScheduleDetail = examSchedule.date,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                )
            )
            ExamsDetailsRow(
                labelResID = R.string.day,
                examScheduleDetail = examSchedule.day,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                )
            )

            ExamsDetailsRow(
                labelResID = R.string.time_start,
                examScheduleDetail = examSchedule.time.format(timeFormatter),
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                )
            )
            ExamsDetailsRow(
                labelResID = R.string.time_end,
                examScheduleDetail = examSchedule.timeEnd.format(timeFormatter),
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                )
            )
        }
    }
}

@Composable
private fun ExamsDetailsRow(
    @StringRes labelResID: Int, examScheduleDetail: String, modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(
            text = stringResource(labelResID),
            modifier = Modifier.weight(0.5f),
            fontWeight = FontWeight.Bold,
            maxLines = 4
        )
        Text(
            text = examScheduleDetail,
            modifier = Modifier.weight(0.8f),
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