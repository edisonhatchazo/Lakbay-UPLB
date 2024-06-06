package com.example.classschedule.ui.home

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.classschedule.data.ClassSchedule
import com.example.classschedule.data.ClassScheduleRepository
import java.time.LocalTime

class ScheduleEntryViewModel(private val classScheduleRepository: ClassScheduleRepository) : ViewModel() {
    var scheduleUiState by mutableStateOf(ScheduleUiState())
        private set

    private val _selectedDays = mutableStateOf<List<String>>(listOf())
    val selectedDays: State<List<String>> = _selectedDays
    fun updateUiState(scheduleDetails: ScheduleDetails) {
        scheduleUiState = ScheduleUiState(
            scheduleDetails = scheduleDetails,
            isEntryValid = validateInput(scheduleDetails)
        )
    }

    fun updateTime(time: LocalTime) {
        val newTime = runCatching { time }.getOrElse { scheduleUiState.scheduleDetails.time }
        updateUiState(scheduleUiState.scheduleDetails.copy(time = newTime))
    }

    fun updateTimeEnd(timeEnd: LocalTime) {
        val newTimeEnd = runCatching { timeEnd }.getOrElse { scheduleUiState.scheduleDetails.timeEnd }
        updateUiState(scheduleUiState.scheduleDetails.copy(timeEnd = newTimeEnd))
    }

    fun updateDays(day: String, isSelected: Boolean) {
        val currentDays = _selectedDays.value.toMutableList()
        if (isSelected && currentDays.size < 2) {
            if (!currentDays.contains(day)) {
                currentDays.add(day)
            }
        } else if (!isSelected) {
            currentDays.remove(day)
        }
        _selectedDays.value = currentDays
        val updatedDetails = scheduleUiState.scheduleDetails.copy(days = _selectedDays.value)
        updateUiState(updatedDetails)
    }
    private fun validateInput(uiState: ScheduleDetails = scheduleUiState.scheduleDetails): Boolean {
        return with(uiState) {
            title.isNotBlank() && location.isNotBlank() && days.isNotEmpty() && time != LocalTime.MIDNIGHT && timeEnd != LocalTime.MIDNIGHT
        }
    }

    suspend fun saveSchedule() {
        if (validateInput()) {
            classScheduleRepository.insertClassSchedule(scheduleUiState.scheduleDetails.toClass())
        }
    }

}

data class ScheduleUiState(
    val scheduleDetails: ScheduleDetails = ScheduleDetails(),
    val isEntryValid: Boolean = false
)

data class ScheduleDetails(
    val id: Int = 0,
    val title: String = "",
    val location: String = "",
    val days: List<String> = listOf(), // Changed from 'day' to 'days'
    val time: LocalTime = LocalTime.of(0, 0),
    val timeEnd: LocalTime = LocalTime.of(0, 0),
    val colorName: String = ""
)

fun ScheduleDetails.toClass(): ClassSchedule = ClassSchedule(
    id = id,
    title = title,
    location = location,
    days = days.joinToString(", "),  // Convert list of days to a comma-separated string
    time = time,
    timeEnd = timeEnd,
    colorName = colorName
)

fun ClassSchedule.toScheduleUiState(isEntryValid: Boolean = false): ScheduleUiState = ScheduleUiState(
    scheduleDetails = this.toScheduleDetails(),
    isEntryValid = isEntryValid
)

fun ClassSchedule.toScheduleDetails(): ScheduleDetails = ScheduleDetails(
    id = id,
    title = title,
    location = location,
    days = days.split(", ").filterNot { it.isEmpty() },  // Convert the string back to a list
    time = time,
    timeEnd = timeEnd,
    colorName = colorName
)