package com.example.classschedule.ui.classes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.classschedule.data.ClassSchedule
import com.example.classschedule.data.ClassScheduleRepository
import java.time.LocalTime
import androidx.compose.runtime.State
import com.example.classschedule.ui.theme.ColorPalette

class ClassScheduleEntryViewModel(private val classScheduleRepository: ClassScheduleRepository) : ViewModel() {
    var classScheduleUiState by mutableStateOf(ClassScheduleUiState())
        private set

    private val _selectedDays = mutableStateOf<List<String>>(listOf())
    val selectedDays: State<List<String>> = _selectedDays
    fun updateUiState(classScheduleDetails: ClassScheduleDetails) {
        classScheduleUiState = ClassScheduleUiState(
            classScheduleDetails = classScheduleDetails,
            isEntryValid = validateInput(classScheduleDetails)
        )
    }

    fun updateTime(time: LocalTime) {
        val newTime = runCatching { time }.getOrElse { classScheduleUiState.classScheduleDetails.time }
        val updatedDetails = classScheduleUiState.classScheduleDetails.copy(time = newTime)
        updateUiState(updatedDetails)
    }

    fun updateTimeEnd(timeEnd: LocalTime) {
        val newTimeEnd = runCatching { timeEnd }.getOrElse { classScheduleUiState.classScheduleDetails.timeEnd }
        val updatedDetails = classScheduleUiState.classScheduleDetails.copy(timeEnd = newTimeEnd)
        updateUiState(updatedDetails)
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
        val updatedDetails = classScheduleUiState.classScheduleDetails.copy(days = _selectedDays.value)
        updateUiState(updatedDetails)
    }

    fun getRandomColorName(): String {
        return ColorPalette.colors.keys.random()
    }

    private fun validateInput(uiState: ClassScheduleDetails = classScheduleUiState.classScheduleDetails): Boolean {
        return with(uiState) {
            title.isNotBlank() && location.isNotBlank() && days.isNotEmpty() && time != LocalTime.MIDNIGHT && timeEnd != LocalTime.MIDNIGHT
        }
    }

    suspend fun saveClassSchedule() {
        if (validateInput()) {
            classScheduleRepository.insertClassSchedule(classScheduleUiState.classScheduleDetails.toClass())
        }
    }

}

data class ClassScheduleUiState(
    val classScheduleDetails: ClassScheduleDetails = ClassScheduleDetails(),
    val isEntryValid: Boolean = false
)

data class ClassScheduleDetails(
    val id: Int = 0,
    val title: String = "",
    val location: String = "",
    val days: List<String> = listOf(), // Changed from 'day' to 'days'
    val time: LocalTime = LocalTime.of(0, 0),
    val timeEnd: LocalTime = LocalTime.of(0, 0),
    val colorName: String = ""
)

fun ClassScheduleDetails.toClass(): ClassSchedule = ClassSchedule(
    id = id,
    title = title,
    location = location,
    days = days.joinToString(", "),  // Convert list of days to a comma-separated string
    time = time,
    timeEnd = timeEnd,
    colorName = colorName
)

fun ClassSchedule.toClassScheduleDetails(): ClassScheduleDetails = ClassScheduleDetails(
    id = id,
    title = title,
    location = location,
    days = days.split(", ").filterNot { it.isEmpty() },  // Convert the string back to a list
    time = time,
    timeEnd = timeEnd,
    colorName = colorName
)

fun ClassSchedule.toClassScheduleUiState(isEntryValid: Boolean = false): ClassScheduleUiState = ClassScheduleUiState(
    classScheduleDetails = this.toClassScheduleDetails(),
    isEntryValid = isEntryValid
)
