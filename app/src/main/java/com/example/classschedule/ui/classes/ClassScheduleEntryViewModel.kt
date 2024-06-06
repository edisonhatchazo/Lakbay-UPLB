package com.example.classschedule.ui.classes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.classschedule.data.ClassSchedule
import com.example.classschedule.data.ClassScheduleRepository
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ClassScheduleEntryViewModel(private val classScheduleRepository: ClassScheduleRepository) : ViewModel() {
    var classScheduleUiState by mutableStateOf(ClassScheduleUiState())
        private set

    private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("h:mm a")

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

    private fun validateInput(uiState: ClassScheduleDetails = classScheduleUiState.classScheduleDetails): Boolean {
        return with(uiState) {
            title.isNotBlank() && location.isNotBlank() && day.isNotBlank() && time != LocalTime.MIDNIGHT && timeEnd != LocalTime.MIDNIGHT
        }
    }

    suspend fun saveClassSchedule() {
        if (validateInput()) {
            classScheduleRepository.insertClassSchedule(classScheduleUiState.classScheduleDetails.toClass())
        }
    }

    fun formatTime(time: LocalTime): String {
        return time.format(timeFormatter)
    }

    fun parseTime(timeString: String): LocalTime {
        return LocalTime.parse(timeString, timeFormatter)
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
    val day: String = "",
    val time: LocalTime = LocalTime.of(0, 0),
    val timeEnd: LocalTime = LocalTime.of(0, 0),
)

fun ClassScheduleDetails.toClass(): ClassSchedule = ClassSchedule(
    id = id,
    title = title,
    location = location,
    day = day,
    time = time,
    timeEnd = timeEnd
)

fun ClassSchedule.toClassScheduleUiState(isEntryValid: Boolean = false): ClassScheduleUiState = ClassScheduleUiState(
    classScheduleDetails = this.toClassScheduleDetails(),
    isEntryValid = isEntryValid
)

fun ClassSchedule.toClassScheduleDetails(): ClassScheduleDetails = ClassScheduleDetails(
    id = id,
    title = title,
    location = location,
    day = day,
    time = time,
    timeEnd = timeEnd
)