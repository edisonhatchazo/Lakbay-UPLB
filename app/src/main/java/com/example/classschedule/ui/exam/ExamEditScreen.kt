package com.example.classschedule.ui.exam

import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.classschedule.R
import com.example.classschedule.algorithm.calculateExamAvailableEndTimes
import com.example.classschedule.algorithm.calculateExamAvailableStartTimes
import com.example.classschedule.ui.navigation.AppViewModelProvider
import com.example.classschedule.ui.navigation.NavigationDestination
import com.example.classschedule.ui.screen.EntryScreenTopAppBar
import kotlinx.coroutines.launch

object ExamEditDestination: NavigationDestination {
    override val route = "exam_edit"
    override val titleRes = R.string.edit_exam_title
    const val SCHEDULEIDARG = "scheduleId"
    val routeWithArgs = "$route/{$SCHEDULEIDARG}"
}

@Composable
fun ExamEditScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExamEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val existingSchedules by viewModel.existingSchedules.collectAsState()

    val examScheduleUiState = viewModel.examScheduleUiState

    val selectedDay = examScheduleUiState.examScheduleDetails.day
    val selectedDate = examScheduleUiState.examScheduleDetails.date
    val availableStartTimes = calculateExamAvailableStartTimes(existingSchedules, selectedDay, selectedDate)
    val availableEndTimes = calculateExamAvailableEndTimes(existingSchedules, selectedDay,selectedDate, examScheduleUiState.examScheduleDetails.time)

    Scaffold(
        topBar = {
            EntryScreenTopAppBar(
                title = stringResource(ExamEditDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        },
        modifier = modifier
    ) { innerPadding ->
        ExamEntryBody(
            examUiState = viewModel.examScheduleUiState,
            onExamValueChange = viewModel::updateUiState,
            availableStartTimes = availableStartTimes,
            availableEndTimes = availableEndTimes,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.updateSchedule()
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