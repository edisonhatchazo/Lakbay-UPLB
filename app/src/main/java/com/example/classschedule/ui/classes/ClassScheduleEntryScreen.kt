package com.example.classschedule.ui.classes

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.classschedule.R
import com.example.classschedule.ui.AppViewModelProvider
import com.example.classschedule.ui.navigation.NavigationDestination
import com.example.classschedule.ui.theme.ClassScheduleTheme
import kotlinx.coroutines.launch
import java.time.LocalTime

object ClassScheduleEntryDestination: NavigationDestination {
    override val route = "class_schedule_entry"
    override val titleRes = R.string.class_entry_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassScheduleEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: ClassScheduleEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            ClassScheduleTopAppBar(
                title = stringResource(ClassScheduleEntryDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp)
        }
    ) { innerPadding ->
        ClassScheduleEntryBody(
            classScheduleUiState = viewModel.classScheduleUiState,
            onClassScheduleValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveClassSchedule()
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
fun ClassScheduleEntryBody(
    classScheduleUiState: ClassScheduleUiState,
    onClassScheduleValueChange: (ClassScheduleDetails) -> Unit,
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
            classScheduleDetails = classScheduleUiState.classScheduleDetails,
            onValueChange = onClassScheduleValueChange,
            onTimeChange = onTimeChange,
            onTimeEndChange = onTimeEndChange,
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
    modifier: Modifier = Modifier,
    onValueChange: (ClassScheduleDetails) -> Unit = {},
    onTimeChange: (LocalTime) -> Unit = {},
    onTimeEndChange: (LocalTime) -> Unit = {},
    enabled: Boolean = true
) {

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        OutlinedTextField(
            value = classScheduleDetails.title,
            onValueChange = { onValueChange(classScheduleDetails.copy(title = it)) },
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
            value = classScheduleDetails.location,
            onValueChange = { onValueChange(classScheduleDetails.copy(location = it)) },
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
            value = classScheduleDetails.day,
            onValueChange = { onValueChange(classScheduleDetails.copy(day = it)) },
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
            initialTime = classScheduleDetails.time,
            onTimeChanged = onTimeChange,
            enabled = enabled
        )

        Text("End Time")
        TimePickerWheel(
            initialTime = classScheduleDetails.timeEnd,
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


@Preview(showBackground = true)
@Composable
private fun ClassScheduleEntryScreenPreview() {
    ClassScheduleTheme {
        ClassScheduleEntryBody(
            classScheduleUiState = ClassScheduleUiState(
                ClassScheduleDetails(
                    title = "Math",
                    location = "MB101",
                    day = "Wednesday",
                    time = LocalTime.of(7, 0),
                    timeEnd = LocalTime.of(9, 0)
                )
            ),
            onClassScheduleValueChange = {},
            onSaveClick = {},
            onTimeChange = {},
            onTimeEndChange = {}
        )
    }
}