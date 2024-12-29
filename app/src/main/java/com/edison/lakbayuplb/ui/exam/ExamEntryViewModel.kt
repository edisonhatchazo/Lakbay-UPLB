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
import com.edison.lakbayuplb.ui.settings.colors.ColorSchemesUiState
import com.edison.lakbayuplb.ui.settings.colors.toColorSchemeUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalTime

class ExamEntryViewModel(
    savedStateHandle: SavedStateHandle,
    private val examScheduleRepository: ExamScheduleRepository,
    private val colorSchemesRepository: ColorSchemesRepository
) : ViewModel()  {
    var examScheduleUiState by mutableStateOf(ExamScheduleUiState())
        private set
    var colorSchemesUiState by mutableStateOf(
        savedStateHandle.get<ColorSchemesUiState>("colorSchemeUiState") ?: ColorSchemesUiState()
    )
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
            title.isNotBlank() &&
            roomId != 0 &&
            location.isNotBlank() &&
            day.isNotBlank() &&
            time != LocalTime.MIDNIGHT &&
            timeEnd != LocalTime.MIDNIGHT  &&
            timeEnd.isAfter(time)
        }
    }

    suspend fun saveSchedule() {
        if (validateInput()) {
            examScheduleRepository.insertExamSchedule(examScheduleUiState.examScheduleDetails.toExam())
            colorSchemesRepository.incrementIsCurrentlyUsed(examScheduleUiState.examScheduleDetails.colorId)
        }
    }

    suspend fun getColor(id: Int){
        viewModelScope.launch{
            val colorScheme = colorSchemesRepository.getColorSchemeById(id)
                .filterNotNull()
                .first()
            colorSchemesUiState = colorScheme.toColorSchemeUiState()
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
    val section: String = "",
    val teacher: String = "",
    val location: String = "",
    val day: String = "",
    val time: LocalTime = LocalTime.of(7, 0),
    val timeEnd: LocalTime = LocalTime.of(7, 0),
    val colorId: Int = 1,
    val date: String = "",
    val roomId: Int = 0

)

fun ExamScheduleDetails.toExam(): ExamSchedule = ExamSchedule(
    id = id,
    title = title,
    section = section,
    teacher = teacher,
    location = location,
    date = date,
    time = time,
    timeEnd = timeEnd,
    day = day,
    roomId = roomId,
    colorId = colorId
)

fun ExamSchedule.toExamScheduleDetails(): ExamScheduleDetails = ExamScheduleDetails(
    id = id,
    title = title,
    section = section,
    teacher = teacher,
    location = location,
    date = date,
    time = time,
    timeEnd = timeEnd,
    day = day,
    roomId = roomId,
    colorId = colorId
)

fun ExamSchedule.toExamScheduleUiState(isEntryValid: Boolean = false): ExamScheduleUiState = ExamScheduleUiState(
    examScheduleDetails = this.toExamScheduleDetails(),
    isEntryValid = isEntryValid
)