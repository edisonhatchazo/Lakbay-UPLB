package com.example.classschedule.ui.exam

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classschedule.data.ExamSchedule
import com.example.classschedule.data.ExamScheduleRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalTime

class ExamEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val examScheduleRepository: ExamScheduleRepository
) : ViewModel() {
    private val scheduleId: Int = checkNotNull(savedStateHandle[ExamEditDestination.SCHEDULEIDARG])
    val existingSchedules: StateFlow<List<ExamSchedule>> = examScheduleRepository.getAllExamsSchedules()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    var examScheduleUiState by mutableStateOf(
        savedStateHandle.get<ExamScheduleUiState>("examScheduleUiState") ?: ExamScheduleUiState()
    )
        private set

    init {
        viewModelScope.launch {
            val schedule = examScheduleRepository.getExamSchedule(scheduleId)
                .filterNotNull()
                .first()
            examScheduleUiState = schedule.toExamScheduleUiState(true)
        }
    }

    fun updateUiState(examScheduleDetails: ExamScheduleDetails) {
        examScheduleUiState = ExamScheduleUiState(
            examScheduleDetails = examScheduleDetails,
            isEntryValid = validateInput(examScheduleDetails)
        )
    }

    private fun validateInput(uiState: ExamScheduleDetails = examScheduleUiState.examScheduleDetails): Boolean {
        return with(uiState) {
            title.isNotBlank()&& roomId != 0 && location.isNotBlank() && day.isNotBlank() && date.isNotBlank() && time != LocalTime.MIDNIGHT && timeEnd != LocalTime.MIDNIGHT
        }
    }

    suspend fun updateSchedule() {
        if (validateInput()) {
            examScheduleRepository.updateExamSchedule(examScheduleUiState.examScheduleDetails.toExam())
        }
    }
}