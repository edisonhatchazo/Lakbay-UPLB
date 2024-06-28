package com.example.classschedule.ui.exam

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classschedule.data.BuildingRepository
import com.example.classschedule.data.ExamScheduleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ExamDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val examScheduleRepository: ExamScheduleRepository,
    private val classroomRepository: BuildingRepository
) : ViewModel() {
    private val classScheduleId: Int = checkNotNull(savedStateHandle[ExamDetailsDestination.SCHEDULEIDARG])

    private val _uiState = MutableStateFlow(ExamScheduleDetailsUiState())
    val uiState: StateFlow<ExamScheduleDetailsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val schedule = examScheduleRepository.getExamSchedule(classScheduleId).filterNotNull().first()
            val room = classroomRepository.getRoom(schedule.roomId).firstOrNull()
            _uiState.value = ExamScheduleDetailsUiState(
                examScheduleDetails = schedule.toExamScheduleDetails(),
                latitude = room?.latitude ?: 0.0,
                longitude = room?.longitude ?: 0.0
            )
        }
    }

    suspend fun deleteExamSchedule() {
        examScheduleRepository.deleteExamSchedule(uiState.value.examScheduleDetails.toExam())
    }

}

data class ExamScheduleDetailsUiState(
    val examScheduleDetails: ExamScheduleDetails = ExamScheduleDetails(),
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)