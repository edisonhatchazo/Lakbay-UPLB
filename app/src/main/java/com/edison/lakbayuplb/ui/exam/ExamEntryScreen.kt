package com.edison.lakbayuplb.ui.exam

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
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.edison.lakbayuplb.algorithm.DatePickers
import com.edison.lakbayuplb.algorithm.SearchViewModel
import com.edison.lakbayuplb.algorithm.TimePickerWheel
import com.edison.lakbayuplb.algorithm.calculateExamAvailableEndTimes
import com.edison.lakbayuplb.algorithm.calculateExamAvailableStartTimes
import com.edison.lakbayuplb.ui.navigation.AppViewModelProvider
import com.edison.lakbayuplb.ui.navigation.NavigationDestination
import com.edison.lakbayuplb.ui.screen.EntryScreenTopAppBar
import com.edison.lakbayuplb.ui.settings.global.TopAppBarColorSchemesViewModel
import com.edison.lakbayuplb.ui.theme.ColorPalette.getColorEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalTime

object ExamEntryDestination: NavigationDestination {
    override val route = "exam_entry"
    override val titleRes = R.string.exam_entry_title
}
@Composable
fun ExamEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    navigateToAboutPage: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: ExamEntryViewModel = viewModel(factory = AppViewModelProvider.Factory),
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
    Scaffold(
        topBar = {
            EntryScreenTopAppBar(
                title = stringResource(ExamEntryDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp,
                navigateToAboutPage = navigateToAboutPage,
                topAppBarBackgroundColor = topAppBarBackgroundColor,
                topAppBarForegroundColor = topAppBarForegroundColor
            )
        }
    ) { innerPadding ->
        ExamEntryBody(
            examUiState = viewModel.examScheduleUiState,
            onExamValueChange = viewModel::updateUiState,
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
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                )
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        )
    }
}

@Composable
fun ExamEntryBody(
    examUiState: ExamScheduleUiState,
    onExamValueChange: (ExamScheduleDetails) -> Unit,
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
        ExamInputForm(
            examDetails = examUiState.examScheduleDetails,
            onValueChange = { onExamValueChange(it)
                isLocationValid.value = it.roomId != 0},
            availableStartTimes = availableStartTimes,
            availableEndTimes = availableEndTimes,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            enabled = examUiState.isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.save_action))
        }
    }
}

@Composable
fun ExamInputForm(
    examDetails: ExamScheduleDetails,
    onValueChange: (ExamScheduleDetails) -> Unit,
    availableStartTimes: List<LocalTime>,
    availableEndTimes: List<LocalTime>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    scheduleViewModel: ExamEntryViewModel = viewModel(factory = AppViewModelProvider.Factory),
    viewModel: SearchViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var showColorPicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    var showRoomSuggestions by remember { mutableStateOf(false) }
    val roomSuggestions by viewModel.roomSuggestions.collectAsState()
    val isLocationValid = remember { mutableStateOf(false) }
    val colorEntry = getColorEntry(examDetails.colorId)
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        OutlinedTextField(
            value = examDetails.title,
            onValueChange = { onValueChange(examDetails.copy(title = it)) },
            label = { Text("Course Number*") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = examDetails.section,
            onValueChange = { onValueChange(examDetails.copy(section = it)) },
            label = { Text("Section*") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = examDetails.teacher,
            onValueChange = { onValueChange(examDetails.copy(teacher = it)) },
            label = { Text("Teacher*") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        Box {
            Column {
                OutlinedTextField(
                    value = examDetails.location,
                    onValueChange = {
                        onValueChange(examDetails.copy(location = it))
                        showRoomSuggestions = true
                        viewModel.updateSearchQuery(it)
                    },
                    label = { Text("Location*") },
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
                                        onValueChange(examDetails.copy(location = room.title, roomId = room.roomId))
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

        Text(text = stringResource(R.string.date_select))
        OutlinedButton(
            onClick = { showDatePicker = true },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = examDetails.date)
        }

        if (showDatePicker) {
            DatePickers(
                initialDate = examDetails.date,
                onDateSelected = { selectedDate, dayOfWeek ->
                    if (dayOfWeek != "SU") {
                        onValueChange(examDetails.copy(date = selectedDate, day = dayOfWeek))
                    } else {
                        snackbarMessage = "Sundays are not allowed. Please select another day."
                    }
                    showDatePicker = false
                },
                onDismiss = { showDatePicker = false }
            )
        }

        if (snackbarMessage.isNotEmpty()) {
            Snackbar(
                action = {
                    TextButton(onClick = { snackbarMessage = "" }) {
                        Text("DISMISS")
                    }
                },
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
            ) {
                Text(snackbarMessage)
            }
        }

        Text(text = "Start Time*")
        TimePickerWheel(
            initialTime = examDetails.time,
            onTimeChanged = { newTime ->
                onValueChange(examDetails.copy(time = newTime))
            },
            availableTimes = availableStartTimes,
            enabled = enabled
        )
        Text(text = "End Time*")
        TimePickerWheel(
            initialTime = examDetails.timeEnd,
            onTimeChanged = { newEndTime ->
                onValueChange(examDetails.copy(timeEnd = newEndTime))
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
                .background(color = colorEntry.backgroundColor)
        ) {
            Text("Select Color*", color = colorEntry.fontColor)
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
                onValueChange(examDetails.copy(colorId = colorId))
                coroutineScope.launch{
                    scheduleViewModel.getColor(colorId)
                }
                showColorPicker = false
            },
            onDismiss = { showColorPicker = false }
        )
    }
    LaunchedEffect(examDetails.location) {
        withContext(Dispatchers.Main) {
            isLocationValid.value = roomSuggestions.any { it.title == examDetails.location }
            if (!isLocationValid.value) {
                onValueChange(examDetails.copy(roomId = 0))
            }
        }
    }
}