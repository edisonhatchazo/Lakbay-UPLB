package com.example.classschedule.ui.home

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

class ScheduleEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val classScheduleRepository: ClassScheduleRepository
) : ViewModel() {

    private val scheduleId: Int = checkNotNull(savedStateHandle[ScheduleEditDestination.SCHEDULEIDARG])

    var scheduleUiState by mutableStateOf(
        savedStateHandle.get<ScheduleUiState>("scheduleUiState") ?: ScheduleUiState()
    )
        private set

    init {
        viewModelScope.launch{
            scheduleUiState = classScheduleRepository.getClassScheduleStream(scheduleId)
                .filterNotNull()
                .first()
                .toScheduleUiState(true)
        }
    }

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
        val newTimeEnd = runCatching {timeEnd }.getOrElse { scheduleUiState.scheduleDetails.timeEnd }
        updateUiState(scheduleUiState.scheduleDetails.copy(timeEnd = newTimeEnd))
    }

    private fun validateInput(uiState: ScheduleDetails = scheduleUiState.scheduleDetails): Boolean {
        return with(uiState) {
            title.isNotBlank() && location.isNotBlank() && day.isNotBlank() && time != LocalTime.MIDNIGHT && timeEnd != LocalTime.MIDNIGHT
        }
    }

    suspend fun updateSchedule() {
        if (validateInput()) {
            classScheduleRepository.updateClassSchedule(scheduleUiState.scheduleDetails.toClass())
        }
    }

}