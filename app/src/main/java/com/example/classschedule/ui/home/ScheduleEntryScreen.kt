package com.example.classschedule.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.classschedule.R
import com.example.classschedule.algorithm.ColorPickerDialog
import com.example.classschedule.algorithm.DaysSelectionCheckboxes
import com.example.classschedule.algorithm.SearchViewModel
import com.example.classschedule.algorithm.TimePickerWheel
import com.example.classschedule.algorithm.calculateAvailableEndTimes
import com.example.classschedule.algorithm.calculateAvailableStartTimes
import com.example.classschedule.ui.navigation.AppViewModelProvider
import com.example.classschedule.ui.navigation.NavigationDestination
import com.example.classschedule.ui.screen.ScheduleEntryScreenTopAppBar
import com.example.classschedule.ui.theme.ColorPalette.getColorEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalTime


object ScheduleEntryDestination: NavigationDestination {
    override val route = "schedule_entry"
    override val titleRes = R.string.class_entry_title
}
@Composable
fun ScheduleEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: ScheduleEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val existingSchedules by viewModel.existingSchedules.collectAsState()
    val selectedDays = viewModel.selectedDays.value
    val scheduleUiState = viewModel.scheduleUiState
    val availableStartTimes = calculateAvailableStartTimes(existingSchedules, selectedDays)
    val availableEndTimes = calculateAvailableEndTimes(existingSchedules, selectedDays, scheduleUiState.scheduleDetails.time)
    Scaffold(
        topBar = {
            ScheduleEntryScreenTopAppBar(
                title = stringResource(ScheduleEntryDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp)
        }
    ) { innerPadding ->
        ScheduleEntryBody(
            scheduleUiState = viewModel.scheduleUiState,
            onScheduleValueChange = viewModel::updateUiState,
            selectedDays = viewModel.selectedDays.value, // Make sure this is handled in ViewModel
            onDaysChange = viewModel::updateDays,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveSchedule()
                    navigateBack()
                }
            },
            availableStartTimes = availableStartTimes,
            availableEndTimes = availableEndTimes,
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
    selectedDays: List<String>,
    onDaysChange: (String, Boolean) -> Unit,
    onScheduleValueChange: (ScheduleDetails) -> Unit,
    onSaveClick: () -> Unit,
    availableStartTimes: List<LocalTime>,
    availableEndTimes: List<LocalTime>,
    modifier: Modifier = Modifier
) {
    val isLocationValid = remember { mutableStateOf(false) }
    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large))
    ) {
        ClassInputForm(
            scheduleDetails = scheduleUiState.scheduleDetails,
            onValueChange = {
                onScheduleValueChange(it)
                isLocationValid.value = it.roomId != 0},
            availableStartTimes = availableStartTimes,
            availableEndTimes = availableEndTimes,
            onDaysChange = onDaysChange,
            selectedDays = selectedDays,
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
    onValueChange: (ScheduleDetails) -> Unit,
    availableStartTimes: List<LocalTime>,
    availableEndTimes: List<LocalTime>,
    selectedDays: List<String>,
    onDaysChange: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    viewModel: SearchViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var showColorPicker by remember { mutableStateOf(false) }
    var showRoomSuggestions by remember { mutableStateOf(false) }
    val roomSuggestions by viewModel.roomSuggestions.collectAsState()
    val isLocationValid = remember { mutableStateOf(false) }
    // State to hold dynamically updated end time options
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        OutlinedTextField(
            value = scheduleDetails.title,
            onValueChange = { onValueChange(scheduleDetails.copy(title = it)) },
            label = { Text(stringResource(R.string.class_name_req)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = scheduleDetails.teacher,
            onValueChange = { onValueChange(scheduleDetails.copy(teacher = it)) },
            label = { Text(stringResource(R.string.teacher)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        Box {
            Column {
                OutlinedTextField(
                    value = scheduleDetails.location,
                    onValueChange = {
                        onValueChange(scheduleDetails.copy(location = it))
                        showRoomSuggestions = true
                        viewModel.updateSearchQuery(it)
                    },
                    label = { Text(stringResource(R.string.location)) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = enabled,
                    singleLine = true,
                    isError = !isLocationValid.value
                )
                if (showRoomSuggestions && roomSuggestions.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .heightIn(max = 200.dp)
                            .border(BorderStroke(1.dp, Color.Gray))
                    ) {
                        LazyColumn {
                            items(roomSuggestions) { room ->
                                DropdownMenuItem(
                                    onClick = {
                                        onValueChange(scheduleDetails.copy(location = room.title, roomId = room.roomId))
                                        showRoomSuggestions = false
                                        isLocationValid.value = true
                                    },
                                    text = { Text(room.title) }
                                )
                            }
                        }
                    }
                }
            }
        }
        Text(text = stringResource(R.string.day_select))
        DaysSelectionCheckboxes(
            selectedDays = selectedDays,
            onDaySelected = onDaysChange,
            modifier = Modifier.fillMaxWidth()
        )
        Text(text = stringResource(R.string.time_start))
        TimePickerWheel(
            initialTime = scheduleDetails.time,
            onTimeChanged = { newTime ->
                onValueChange(scheduleDetails.copy(time = newTime))
            },
            availableTimes = availableStartTimes,
            enabled = enabled
        )
        Text(text = stringResource(R.string.time_end))
        TimePickerWheel(
            initialTime = scheduleDetails.timeEnd,
            onTimeChanged = { newEndTime ->
                onValueChange(scheduleDetails.copy(timeEnd = newEndTime))
            },
            availableTimes = availableEndTimes,
            enabled = enabled
        )
        OutlinedButton(
            onClick = { showColorPicker = true },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(color = getColorEntry(scheduleDetails.colorName).backgroundColor)
        ) {
            Text("Select Color", color = getColorEntry(scheduleDetails.colorName).fontColor)
        }
        if (enabled) {
            Text(
                text = stringResource(R.string.required_fields),
                modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_medium))
            )
        }
    }

    if (showColorPicker) {
        ColorPickerDialog(
            onColorSelected = { colorName ->
                onValueChange(scheduleDetails.copy(colorName = colorName))
                showColorPicker = false
            },
            onDismiss = { showColorPicker = false }
        )
    }


    LaunchedEffect(scheduleDetails.location) {
        withContext(Dispatchers.Main) {
            isLocationValid.value = roomSuggestions.any { it.title == scheduleDetails.location }
            if (!isLocationValid.value) {
                onValueChange(scheduleDetails.copy(roomId = 0))
            }
        }
    }
}