package com.edison.lakbayuplb.ui.classes

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
import com.edison.lakbayuplb.R
import com.edison.lakbayuplb.algorithm.ColorPickerDialog
import com.edison.lakbayuplb.algorithm.DaysSelectionCheckboxes
import com.edison.lakbayuplb.algorithm.SearchViewModel
import com.edison.lakbayuplb.algorithm.TimePickerWheel
import com.edison.lakbayuplb.algorithm.calculateAvailableEndTimes
import com.edison.lakbayuplb.algorithm.calculateAvailableStartTimes
import com.edison.lakbayuplb.ui.navigation.AppViewModelProvider
import com.edison.lakbayuplb.ui.navigation.NavigationDestination
import com.edison.lakbayuplb.ui.screen.EntryScreenTopAppBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalTime

object ClassScheduleEntryDestination: NavigationDestination {
    override val route = "class_schedule_entry"
    override val titleRes = R.string.class_entry_title
}

@Composable
fun ClassScheduleEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: ClassScheduleEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val existingSchedules by viewModel.existingSchedules.collectAsState()
    val selectedDays = viewModel.selectedDays.value
    val classScheduleUiState = viewModel.classScheduleUiState

    val availableStartTimes = calculateAvailableStartTimes(existingSchedules, selectedDays)
    val availableEndTimes = calculateAvailableEndTimes(existingSchedules, selectedDays, classScheduleUiState.classScheduleDetails.time)

    Scaffold(
        topBar = {
            EntryScreenTopAppBar(
                title = stringResource(ClassScheduleEntryDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                )
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        ) {
            ClassScheduleEntryBody(
                classScheduleUiState = classScheduleUiState,
                onClassScheduleValueChange = viewModel::updateUiState,
                selectedDays = selectedDays, // Make sure this is handled in ViewModel
                onDaysChange = viewModel::updateDays,
                onSaveClick = {
                    coroutineScope.launch {
                        viewModel.saveClassSchedule()
                        navigateBack()
                    }
                },
                availableStartTimes = availableStartTimes,
                availableEndTimes = availableEndTimes,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ClassScheduleEntryBody(
    classScheduleUiState: ClassScheduleUiState,
    selectedDays: List<String>,
    onDaysChange: (String, Boolean) -> Unit,
    onClassScheduleValueChange: (ClassScheduleDetails) -> Unit,
    onSaveClick: () -> Unit,
    availableStartTimes: List<LocalTime>,
    availableEndTimes: List<LocalTime>,
    modifier: Modifier = Modifier
) {
    val isLocationValid = remember { mutableStateOf(false) }
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large)),
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        ClassInputForm(
            classScheduleDetails = classScheduleUiState.classScheduleDetails,
            selectedDays = selectedDays,
            onDaysChange = onDaysChange,
            onValueChange = {
                onClassScheduleValueChange(it)
                isLocationValid.value = it.roomId != 0    },
            availableStartTimes = availableStartTimes,
            availableEndTimes = availableEndTimes,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            enabled = classScheduleUiState.isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.save_action))
        }
    }
}

@Composable
fun ClassInputForm(
    classScheduleDetails: ClassScheduleDetails,
    onValueChange: (ClassScheduleDetails) -> Unit,
    availableStartTimes: List<LocalTime>,
    availableEndTimes: List<LocalTime>,
    selectedDays: List<String>,
    onDaysChange: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    scheduleViewModel: ClassScheduleEntryViewModel = viewModel(factory = AppViewModelProvider.Factory),
    viewModel: SearchViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    var showColorPicker by remember { mutableStateOf(false) }
    var showRoomSuggestions by remember { mutableStateOf(false) }
    val roomSuggestions by viewModel.roomSuggestions.collectAsState()
    val isLocationValid = remember { mutableStateOf(false) }
    val colorEntry = scheduleViewModel.colorSchemesUiState
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        OutlinedTextField(
            value = classScheduleDetails.title,
            onValueChange = { onValueChange(classScheduleDetails.copy(title = it)) },
            label = { Text(stringResource(R.string.class_name_req)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = classScheduleDetails.section,
            onValueChange = { onValueChange(classScheduleDetails.copy(section = it)) },
            label = { Text(stringResource(R.string.section)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = classScheduleDetails.teacher,
            onValueChange = { onValueChange(classScheduleDetails.copy(teacher = it)) },
            label = { Text(stringResource(R.string.teacher)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        Box {
            Column {
                OutlinedTextField(
                    value = classScheduleDetails.location,
                    onValueChange = {
                        onValueChange(classScheduleDetails.copy(location = it))
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
                                        onValueChange(classScheduleDetails.copy(location = room.title, roomId = room.roomId))
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
            initialTime = classScheduleDetails.time,
            onTimeChanged = { newTime ->
                onValueChange(classScheduleDetails.copy(time = newTime))
            },
            availableTimes = availableStartTimes,
            enabled = enabled
        )
        Text(text = stringResource(R.string.time_end))
        TimePickerWheel(
            initialTime = classScheduleDetails.timeEnd,
            onTimeChanged = { newEndTime ->
                onValueChange(classScheduleDetails.copy(timeEnd = newEndTime))
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
                .background(color = Color(colorEntry.colorSchemeDetails.backgroundColor))
        ) {
            Text("Select Color", color = Color(colorEntry.colorSchemeDetails.fontColor))
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
            onColorSelected = { colorId ->
                onValueChange(classScheduleDetails.copy(colorId = colorId))
                coroutineScope.launch{
                    scheduleViewModel.getColor(colorId)
                }
                showColorPicker = false
            },
            onDismiss = { showColorPicker = false }
        )
    }


    LaunchedEffect(classScheduleDetails.location) {
        withContext(Dispatchers.Main) {
            isLocationValid.value =
                roomSuggestions.any { it.title == classScheduleDetails.location }
            if (!isLocationValid.value) {
                onValueChange(classScheduleDetails.copy(roomId = 0))
            }
        }
    }
}