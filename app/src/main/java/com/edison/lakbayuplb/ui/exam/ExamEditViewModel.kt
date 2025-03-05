package com.edison.lakbayuplb.ui.exam

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edison.lakbayuplb.data.classes.ExamSchedule
import com.edison.lakbayuplb.data.classes.ExamScheduleRepository
import com.edison.lakbayuplb.data.colorschemes.ColorSchemesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalTime

class ExamEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val examScheduleRepository: ExamScheduleRepository,
    private val colorSchemesRepository: ColorSchemesRepository
) : ViewModel() {
    val scheduleId: Int = checkNotNull(savedStateHandle[ExamEditDestination.SCHEDULEIDARG])
    val existingSchedules: StateFlow<List<ExamSchedule>> = examScheduleRepository.getAllExamsSchedules()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    var examScheduleUiState by mutableStateOf(
        savedStateHandle.get<ExamScheduleUiState>("examScheduleUiState") ?: ExamScheduleUiState()
    )
        private set
    private var previousColor = 1
    fun loadExamSchedule() {
        viewModelScope.launch {
            val schedule = examScheduleRepository.getExamSchedule(scheduleId)
                .filterNotNull()
                .first()
            examScheduleUiState = schedule.toExamScheduleUiState(true)
            previousColor = examScheduleUiState.examScheduleDetails.colorId
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
            title.isNotBlank() &&
            roomId != 0 &&
            location.isNotBlank() &&
            day.isNotBlank() &&
            date.isNotBlank() &&
            time != LocalTime.MIDNIGHT &&
            timeEnd != LocalTime.MIDNIGHT &&
            timeEnd.isAfter(time)
        }
    }

    suspend fun updateSchedule() {
        if (validateInput()) {
            examScheduleRepository.updateExamSchedule(examScheduleUiState.examScheduleDetails.toExam())
            colorSchemesRepository.decrementIsCurrentlyUsed(previousColor)
            colorSchemesRepository.incrementIsCurrentlyUsed(examScheduleUiState.examScheduleDetails.colorId)
        }
    }
}