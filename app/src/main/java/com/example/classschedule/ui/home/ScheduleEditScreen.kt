package com.example.classschedule.ui.home

import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.classschedule.R
import com.example.classschedule.ui.AppViewModelProvider
import com.example.classschedule.ui.classes.ClassScheduleTopAppBar
import com.example.classschedule.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch

object ScheduleEditDestination: NavigationDestination {
    override val route = "schedule_edit"
    override val titleRes = R.string.edit_item_title
    const val SCHEDULEIDARG = "scheduleId"
    val routeWithArgs = "$route/{$SCHEDULEIDARG}"
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleEditScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ScheduleEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            ClassScheduleTopAppBar(
                title = stringResource(ScheduleEditDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        },
        modifier = modifier
    ) { innerPadding ->
        ScheduleEntryBody(
            scheduleUiState = viewModel.scheduleUiState,
            onScheduleValueChange = viewModel::updateUiState,
            onTimeChange = viewModel::updateTime,
            onTimeEndChange = viewModel::updateTimeEnd,
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
                    top = innerPadding.calculateTopPadding()
                )
                .verticalScroll(rememberScrollState())
        )
    }
}
