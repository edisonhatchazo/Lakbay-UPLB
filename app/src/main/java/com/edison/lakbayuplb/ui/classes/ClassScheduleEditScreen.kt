package com.edison.lakbayuplb.ui.classes

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
import com.edison.lakbayuplb.R
import com.edison.lakbayuplb.algorithm.calculateAvailableEndTimes
import com.edison.lakbayuplb.algorithm.calculateAvailableStartTimes
import com.edison.lakbayuplb.ui.navigation.AppViewModelProvider
import com.edison.lakbayuplb.ui.navigation.NavigationDestination
import com.edison.lakbayuplb.ui.screen.EntryScreenTopAppBar
import com.edison.lakbayuplb.ui.settings.global.TopAppBarColorSchemesViewModel
import kotlinx.coroutines.launch

object ClassScheduleEditDestination: NavigationDestination {
    override val route = "class_schedule_edit"
    override val titleRes = R.string.edit_class_title
    const val CLASSSCHEDULEIDARG = "classScheduleId"
    val routeWithArgs = "$route/{$CLASSSCHEDULEIDARG}"
}

@Composable
fun ClassScheduleEditScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ClassScheduleEditViewModel = viewModel(factory = AppViewModelProvider.Factory),
    colorViewModel: TopAppBarColorSchemesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val topAppBarColors = colorViewModel.topAppBarColors.collectAsState()
    val (topAppBarBackgroundColor, topAppBarForegroundColor) = topAppBarColors.value

    val coroutineScope = rememberCoroutineScope()
    val existingSchedules by viewModel.existingSchedules.collectAsState()
    val selectedDays = viewModel.selectedDays.value
    val classScheduleUiState = viewModel.classScheduleUiState
    val editingClassSchedule = classScheduleUiState.classScheduleDetails.toClass()  // Ensure you convert the UI state to ClassSchedule model here if necessary
    val availableStartTimes = calculateAvailableStartTimes(existingSchedules, selectedDays,editingClassSchedule)
    val availableEndTimes = calculateAvailableEndTimes(existingSchedules, selectedDays, classScheduleUiState.classScheduleDetails.time,editingClassSchedule)

    Scaffold(
        topBar = {
            EntryScreenTopAppBar(
                title = stringResource(ClassScheduleEditDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onNavigateUp,
                topAppBarForegroundColor = topAppBarForegroundColor,
                topAppBarBackgroundColor = topAppBarBackgroundColor
            )
        },
        modifier = modifier
    ) { innerPadding ->
        ClassScheduleEntryBody(
            classScheduleUiState = viewModel.classScheduleUiState,
            selectedDays = viewModel.selectedDays.value, // Access the list from State<List<String>>
            onDaysChange = viewModel::updateDays,
            onClassScheduleValueChange = viewModel::updateUiState,
            availableStartTimes = availableStartTimes,
            availableEndTimes = availableEndTimes,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.updateClassSchedule()
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
