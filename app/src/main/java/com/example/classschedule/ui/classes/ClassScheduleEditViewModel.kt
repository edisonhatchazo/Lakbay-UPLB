package com.example.classschedule.ui.classes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classschedule.data.ClassScheduleRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ClassScheduleEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val classScheduleRepository: ClassScheduleRepository
) : ViewModel() {

    private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("h:mm a")
    private val classScheduleId: Int = checkNotNull(savedStateHandle[ClassScheduleEditDestination.CLASSSCHEDULEIDARG])

    var classScheduleUiState by mutableStateOf(
        savedStateHandle.get<ClassScheduleUiState>("classScheduleUiState") ?: ClassScheduleUiState()
    )
        private set

    init {
        viewModelScope.launch{
            classScheduleUiState = classScheduleRepository.getClassScheduleStream(classScheduleId)
                .filterNotNull()
                .first()
                .toClassScheduleUiState(true)
        }
    }

    fun updateUiState(classScheduleDetails: ClassScheduleDetails) {
        classScheduleUiState = ClassScheduleUiState(
            classScheduleDetails = classScheduleDetails,
            isEntryValid = validateInput(classScheduleDetails)
        )
    }

    fun updateTime(time: LocalTime) {
        val newTime = runCatching { time }.getOrElse { classScheduleUiState.classScheduleDetails.time }
        updateUiState(classScheduleUiState.classScheduleDetails.copy(time = newTime))
    }

    fun updateTimeEnd(timeEnd: LocalTime) {
        val newTimeEnd = runCatching {timeEnd }.getOrElse { classScheduleUiState.classScheduleDetails.timeEnd }
        updateUiState(classScheduleUiState.classScheduleDetails.copy(timeEnd = newTimeEnd))
    }

    private fun validateInput(uiState: ClassScheduleDetails = classScheduleUiState.classScheduleDetails): Boolean {
        return with(uiState) {
            title.isNotBlank() && location.isNotBlank() && day.isNotBlank() && time != LocalTime.MIDNIGHT && timeEnd != LocalTime.MIDNIGHT
        }
    }

    suspend fun updateClassSchedule() {
        if (validateInput()) {
            classScheduleRepository.updateClassSchedule(classScheduleUiState.classScheduleDetails.toClass())
        }
    }

    fun formatTime(time: LocalTime): String {
        return time.format(timeFormatter)
    }

    fun parseTime(timeString: String): LocalTime {
        return LocalTime.parse(timeString, timeFormatter)
    }
}