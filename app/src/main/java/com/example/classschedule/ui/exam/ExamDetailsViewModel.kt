package com.example.classschedule.ui.exam

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classschedule.data.ExamScheduleRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ExamDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val examScheduleRepository: ExamScheduleRepository
) : ViewModel() {
    private val classScheduleId: Int = checkNotNull(savedStateHandle[ExamDetailsDestination.SCHEDULEIDARG])
    val uiState: StateFlow<ExamScheduleDetailsUiState> =
        examScheduleRepository.getExamSchedule(classScheduleId)
            .filterNotNull()
            .map {
                ExamScheduleDetailsUiState(examScheduleDetails = it.toExamScheduleDetails())
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = ExamScheduleDetailsUiState()
            )

    suspend fun deleteExamSchedule() {
        examScheduleRepository.deleteExamSchedule(uiState.value.examScheduleDetails.toExam())
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class ExamScheduleDetailsUiState(
    val examScheduleDetails: ExamScheduleDetails = ExamScheduleDetails()
)