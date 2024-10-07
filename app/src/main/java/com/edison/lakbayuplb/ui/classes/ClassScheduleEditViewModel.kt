package com.edison.lakbayuplb.ui.classes

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edison.lakbayuplb.data.classes.ClassSchedule
import com.edison.lakbayuplb.data.classes.ClassScheduleRepository
import com.edison.lakbayuplb.data.colorschemes.ColorSchemesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalTime

class ClassScheduleEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val classScheduleRepository: ClassScheduleRepository,
    private val colorSchemesRepository: ColorSchemesRepository
) : ViewModel() {

    val classScheduleId: Int = checkNotNull(savedStateHandle[ClassScheduleEditDestination.CLASSSCHEDULEIDARG])
    private val _selectedDays = mutableStateOf<List<String>>(listOf())
    val selectedDays: State<List<String>> = _selectedDays
    val existingSchedules: StateFlow<List<ClassSchedule>> = classScheduleRepository.getAllClassSchedules()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    private var previousColor = 1
    var classScheduleUiState by mutableStateOf(
        savedStateHandle.get<ClassScheduleUiState>("classScheduleUiState") ?: ClassScheduleUiState()
    )
        private set

    fun loadClassSchedule() {
        viewModelScope.launch {
            val schedule = classScheduleRepository.getClassSchedule(classScheduleId)
                .filterNotNull()
                .first()
            _selectedDays.value = schedule.days.split(", ").map { it.trim() }
            classScheduleUiState = schedule.toClassScheduleUiState(true)
            previousColor = classScheduleUiState.classScheduleDetails.colorId
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
    private fun validateInput(uiState: ClassScheduleDetails = classScheduleUiState.classScheduleDetails): Boolean {
        return with(uiState) {
            title.isNotBlank() && roomId != 0 && location.isNotBlank() && days.isNotEmpty() && time != LocalTime.MIDNIGHT && timeEnd != LocalTime.MIDNIGHT
        }
    }

    suspend fun updateClassSchedule() {
        if (validateInput()) {
            classScheduleRepository.updateClassSchedule(classScheduleUiState.classScheduleDetails.toClass())
            colorSchemesRepository.decrementIsCurrentlyUsed(previousColor)
            colorSchemesRepository.incrementIsCurrentlyUsed(classScheduleUiState.classScheduleDetails.colorId)
        }
    }
}