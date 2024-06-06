package com.example.classschedule.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.classschedule.data.ClassSchedule
import com.example.classschedule.data.ClassScheduleRepository
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ScheduleEntryViewModel(private val classScheduleRepository: ClassScheduleRepository) : ViewModel() {
    var scheduleUiState by mutableStateOf(ScheduleUiState())
        private set

    private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("h:mm a")

    fun updateUiState(scheduleDetails: ScheduleDetails) {
        scheduleUiState = ScheduleUiState(
            scheduleDetails = scheduleDetails,
            isEntryValid = validateInput(scheduleDetails)
        )
    }

    fun updateTime(time: LocalTime) {
        val newTime = runCatching { time }.getOrElse { scheduleUiState.scheduleDetails.time }
        val updatedDetails = scheduleUiState.scheduleDetails.copy(time = newTime)
        updateUiState(updatedDetails)
    }

    fun updateTimeEnd(timeEnd: LocalTime) {
        val newTimeEnd = runCatching { timeEnd }.getOrElse { scheduleUiState.scheduleDetails.timeEnd }
        val updatedDetails = scheduleUiState.scheduleDetails.copy(timeEnd = newTimeEnd)
        updateUiState(updatedDetails)
    }

    private fun validateInput(uiState: ScheduleDetails = scheduleUiState.scheduleDetails): Boolean {
        return with(uiState) {
            title.isNotBlank() && location.isNotBlank() && day.isNotBlank() && time != LocalTime.MIDNIGHT && timeEnd != LocalTime.MIDNIGHT
        }
    }

    suspend fun saveSchedule() {
        if (validateInput()) {
            classScheduleRepository.insertClassSchedule(scheduleUiState.scheduleDetails.toClass())
        }
    }

    fun formatTime(time: LocalTime): String {
        return time.format(timeFormatter)
    }

    fun parseTime(timeString: String): LocalTime {
        return LocalTime.parse(timeString, timeFormatter)
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
    val day: String = "",
    val time: LocalTime = LocalTime.of(0, 0),
    val timeEnd: LocalTime = LocalTime.of(0, 0),
)

fun ScheduleDetails.toClass(): ClassSchedule = ClassSchedule(
    id = id,
    title = title,
    location = location,
    day = day,
    time = time,
    timeEnd = timeEnd
)

fun ClassSchedule.toScheduleUiState(isEntryValid: Boolean = false): ScheduleUiState = ScheduleUiState(
    scheduleDetails = this.toScheduleDetails(),
    isEntryValid = isEntryValid
)

fun ClassSchedule.toScheduleDetails(): ScheduleDetails = ScheduleDetails(
    id = id,
    title = title,
    location = location,
    day = day,
    time = time,
    timeEnd = timeEnd
)