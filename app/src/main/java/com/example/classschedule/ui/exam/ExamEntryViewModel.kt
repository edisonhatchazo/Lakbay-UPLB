package com.example.classschedule.ui.exam

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classschedule.data.ExamSchedule
import com.example.classschedule.data.ExamScheduleRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.time.LocalTime

class ExamEntryViewModel(private val examScheduleRepository: ExamScheduleRepository) : ViewModel()  {
    var examScheduleUiState by mutableStateOf(ExamScheduleUiState())
        private set

    val existingSchedules: StateFlow<List<ExamSchedule>> = examScheduleRepository.getAllExamsSchedules()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    fun updateUiState(examScheduleDetails: ExamScheduleDetails) {
        examScheduleUiState = ExamScheduleUiState(
            examScheduleDetails = examScheduleDetails,
            isEntryValid = validateInput(examScheduleDetails)
        )
    }

    private fun validateInput(uiState: ExamScheduleDetails = examScheduleUiState.examScheduleDetails): Boolean {
        return with(uiState) {
            title.isNotBlank() && roomId != 0 && location.isNotBlank() && day.isNotBlank() && time != LocalTime.MIDNIGHT && timeEnd != LocalTime.MIDNIGHT
        }
    }

    suspend fun saveSchedule() {
        if (validateInput()) {
            examScheduleRepository.insertExamSchedule(examScheduleUiState.examScheduleDetails.toExam())

        }
    }
}

data class ExamScheduleUiState(
    val examScheduleDetails: ExamScheduleDetails = ExamScheduleDetails(),
    val isEntryValid: Boolean = false
)

data class ExamScheduleDetails(
    val id: Int = 0,
    val title: String = "",
    val teacher: String = "",
    val location: String = "",
    val day: String = "",
    val time: LocalTime = LocalTime.of(0, 0),
    val timeEnd: LocalTime = LocalTime.of(0, 0),
    val colorName: String = "",
    val date: String = "",
    val roomId: Int = 0

)

fun ExamScheduleDetails.toExam(): ExamSchedule = ExamSchedule(
    id = id,
    title = title,
    teacher = teacher,
    location = location,
    date = date,
    time = time,
    timeEnd = timeEnd,
    colorName = colorName,
    day = day,
    roomId = roomId
)

fun ExamSchedule.toExamScheduleDetails(): ExamScheduleDetails = ExamScheduleDetails(
    id = id,
    title = title,
    teacher = teacher,
    location = location,
    date = date,
    time = time,
    timeEnd = timeEnd,
    colorName = colorName,
    day = day,
    roomId = roomId
)

fun ExamSchedule.toExamScheduleUiState(isEntryValid: Boolean = false): ExamScheduleUiState = ExamScheduleUiState(
    examScheduleDetails = this.toExamScheduleDetails(),
    isEntryValid = isEntryValid
)