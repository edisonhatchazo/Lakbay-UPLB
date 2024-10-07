package com.edison.lakbayuplb.ui.exam

import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edison.lakbayuplb.R
import com.edison.lakbayuplb.algorithm.calculateExamAvailableEndTimes
import com.edison.lakbayuplb.algorithm.calculateExamAvailableStartTimes
import com.edison.lakbayuplb.ui.navigation.AppViewModelProvider
import com.edison.lakbayuplb.ui.navigation.NavigationDestination
import com.edison.lakbayuplb.ui.screen.EntryScreenTopAppBar
import com.edison.lakbayuplb.ui.settings.global.TopAppBarColorSchemesViewModel
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
    viewModel: ExamEditViewModel = viewModel(factory = AppViewModelProvider.Factory),
    colorViewModel: TopAppBarColorSchemesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val topAppBarColors = colorViewModel.topAppBarColors.collectAsState()
    val (topAppBarBackgroundColor, topAppBarForegroundColor) = topAppBarColors.value

    val coroutineScope = rememberCoroutineScope()
    val existingSchedules by viewModel.existingSchedules.collectAsState()

    val examScheduleUiState = viewModel.examScheduleUiState

    val selectedDay = examScheduleUiState.examScheduleDetails.day
    val selectedDate = examScheduleUiState.examScheduleDetails.date
    val availableStartTimes = calculateExamAvailableStartTimes(existingSchedules, selectedDay, selectedDate)
    val availableEndTimes = calculateExamAvailableEndTimes(existingSchedules, selectedDay,selectedDate, examScheduleUiState.examScheduleDetails.time)

    LaunchedEffect(viewModel.scheduleId) {
        viewModel.loadExamSchedule()
    }

    Scaffold(
        topBar = {
            EntryScreenTopAppBar(
                title = stringResource(ExamEditDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onNavigateUp,
                topAppBarBackgroundColor = topAppBarBackgroundColor,
                topAppBarForegroundColor = topAppBarForegroundColor
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