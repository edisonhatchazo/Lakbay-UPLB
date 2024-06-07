package com.example.classschedule.ui.classes

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classschedule.data.ClassSchedule
import com.example.classschedule.data.ClassScheduleRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalTime

class ClassScheduleEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val classScheduleRepository: ClassScheduleRepository
) : ViewModel() {

    private val classScheduleId: Int = checkNotNull(savedStateHandle[ClassScheduleEditDestination.CLASSSCHEDULEIDARG])
    private val _selectedDays = mutableStateOf<List<String>>(listOf())
    val selectedDays: State<List<String>> = _selectedDays
    val existingSchedules: StateFlow<List<ClassSchedule>> = classScheduleRepository.getAllClassScheduleStream()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    var classScheduleUiState by mutableStateOf(
        savedStateHandle.get<ClassScheduleUiState>("classScheduleUiState") ?: ClassScheduleUiState()
    )
        private set

    init {
        viewModelScope.launch{
            val schedule = classScheduleRepository.getClassScheduleStream(classScheduleId)
                .filterNotNull()
                .first()
            _selectedDays.value = schedule.days.split(", ").map { it.trim() }
            classScheduleUiState = schedule.toClassScheduleUiState(true)
        }
    }

    fun updateDays(day: String, isSelected: Boolean) {
        if (isSelected && _selectedDays.value.size < 2) {
            _selectedDays.value += day
        } else if (!isSelected) {
            _selectedDays.value -= day
        }
        updateUiState(classScheduleUiState.classScheduleDetails.copy(days = _selectedDays.value))
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
        val newTimeEnd = runCatching { timeEnd }.getOrElse { classScheduleUiState.classScheduleDetails.timeEnd }
        updateUiState(classScheduleUiState.classScheduleDetails.copy(timeEnd = newTimeEnd))
    }

    private fun validateInput(uiState: ClassScheduleDetails = classScheduleUiState.classScheduleDetails): Boolean {
        return with(uiState) {
            title.isNotBlank() && location.isNotBlank() && days.isNotEmpty() && time != LocalTime.MIDNIGHT && timeEnd != LocalTime.MIDNIGHT
        }
    }

    suspend fun updateClassSchedule() {
        if (validateInput()) {
            classScheduleRepository.updateClassSchedule(classScheduleUiState.classScheduleDetails.toClass())
        }
    }


}