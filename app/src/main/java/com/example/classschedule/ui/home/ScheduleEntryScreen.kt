package com.example.classschedule.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.classschedule.R
import com.example.classschedule.ui.AppViewModelProvider
import com.example.classschedule.ui.classes.ClassScheduleTopAppBar
import com.example.classschedule.ui.classes.TimePickerWheel
import com.example.classschedule.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch
import java.time.LocalTime


object ScheduleEntryDestination: NavigationDestination {
    override val route = "schedule_entry"
    override val titleRes = R.string.class_entry_title
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: ScheduleEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            ClassScheduleTopAppBar(
                title = stringResource(ScheduleEntryDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp)
        }
    ) { innerPadding ->
        ScheduleEntryBody(
            scheduleUiState = viewModel.scheduleUiState,
            onScheduleValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveSchedule()
                    navigateBack()
                }
            },
            onTimeChange = viewModel::updateTime,
            onTimeEndChange = viewModel::updateTimeEnd,
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding()
                )
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        )
    }
}

@Composable
fun ScheduleEntryBody(
    scheduleUiState: ScheduleUiState,
    onScheduleValueChange: (ScheduleDetails) -> Unit,
    onSaveClick: () -> Unit,
    onTimeChange: (LocalTime) -> Unit,
    onTimeEndChange: (LocalTime) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large)),
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        ClassInputForm(
            scheduleDetails = scheduleUiState.scheduleDetails,
            onValueChange = onScheduleValueChange,
            onTimeChange = onTimeChange,
            onTimeEndChange = onTimeEndChange,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            enabled = scheduleUiState.isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.save_action))
        }
    }
}

@Composable
fun ClassInputForm(
    scheduleDetails: ScheduleDetails,
    modifier: Modifier = Modifier,
    onValueChange: (ScheduleDetails) -> Unit = {},
    onTimeChange: (LocalTime) -> Unit = {},
    onTimeEndChange: (LocalTime) -> Unit = {},
    enabled: Boolean = true
) {

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        OutlinedTextField(
            value = scheduleDetails.title,
            onValueChange = { onValueChange(scheduleDetails.copy(title = it)) },
            label = { Text(stringResource(R.string.class_name_req)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = scheduleDetails.location,
            onValueChange = { onValueChange(scheduleDetails.copy(location = it)) },
            label = { Text(stringResource(R.string.location)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = scheduleDetails.day,
            onValueChange = { onValueChange(scheduleDetails.copy(day = it)) },
            label = { Text(stringResource(R.string.day)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )

        Text("Start Time")
        TimePickerWheel(
            initialTime = scheduleDetails.time,
            onTimeChanged = onTimeChange,
            enabled = enabled
        )

        Text("End Time")
        TimePickerWheel(
            initialTime = scheduleDetails.timeEnd,
            onTimeChanged = onTimeEndChange,
            enabled = enabled
        )

        if (enabled) {
            Text(
                text = stringResource(R.string.required_fields),
                modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_medium))
            )
        }
    }
}
