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
import com.edison.lakbayuplb.ui.settings.colors.ColorSchemesUiState
import com.edison.lakbayuplb.ui.settings.colors.toColorSchemeUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import java.time.LocalTime

class ClassScheduleEntryViewModel(
    savedStateHandle: SavedStateHandle,
    private val classScheduleRepository: ClassScheduleRepository,
    private val colorSchemesRepository: ColorSchemesRepository
) : ViewModel() {
    var colorSchemesUiState by mutableStateOf(
        savedStateHandle.get<ColorSchemesUiState>("colorSchemeUiState") ?: ColorSchemesUiState()
    )
    var classScheduleUiState by mutableStateOf(
        ClassScheduleUiState(
            classScheduleDetails = ClassScheduleDetails(),
            isEntryValid = false
        )
    )
        private set

    private val _selectedDays = mutableStateOf<List<String>>(listOf())
    val selectedDays: State<List<String>> = _selectedDays
    val existingSchedules: StateFlow<List<ClassSchedule>> = classScheduleRepository.getAllClassSchedules()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    fun updateUiState(classScheduleDetails: ClassScheduleDetails) {
        classScheduleUiState = ClassScheduleUiState(
            classScheduleDetails = classScheduleDetails,
            isEntryValid = validateInput(classScheduleDetails)
        )
    }

    fun updateDays(day: String, isSelected: Boolean) {
        val currentDays = _selectedDays.value.toMutableList()
        if (isSelected && currentDays.size < 2) {
            if (!currentDays.contains(day)) {
                currentDays.add(day)
            }
        } else if (!isSelected) {
            currentDays.remove(day)
        }
        _selectedDays.value = currentDays
        val updatedDetails = classScheduleUiState.classScheduleDetails.copy(days = _selectedDays.value)
        updateUiState(updatedDetails)
    }

    private fun validateInput(uiState: ClassScheduleDetails = classScheduleUiState.classScheduleDetails): Boolean {
        return with(uiState) {
            title.isNotBlank() && roomId != 0 && location.isNotBlank() && days.isNotEmpty() && time != LocalTime.MIDNIGHT && timeEnd != LocalTime.MIDNIGHT
        }
    }

    suspend fun saveClassSchedule() {
        if (validateInput()) {
            classScheduleRepository.insertClassSchedule(classScheduleUiState.classScheduleDetails.toClass())
            colorSchemesRepository.incrementIsCurrentlyUsed(classScheduleUiState.classScheduleDetails.colorId)
        }
    }
    suspend fun getColor(id: Int): ColorSchemesUiState {
        return withContext(Dispatchers.IO) {
            val colorScheme = colorSchemesRepository.getColorSchemeById(id)
                .filterNotNull()
                .first()
            colorSchemesUiState = colorScheme.toColorSchemeUiState()
            colorSchemesUiState
        }
    }
}

data class ClassScheduleUiState(
    val classScheduleDetails: ClassScheduleDetails = ClassScheduleDetails(),
    val isEntryValid: Boolean = false
)

data class ClassScheduleDetails(
    val id: Int = 0,
    val title: String = "",
    val section: String = "",
    val teacher: String = "",
    val location: String = "",
    val days: List<String> = listOf(), // Changed from 'day' to 'days'
    val time: LocalTime = LocalTime.of(7, 0),
    val timeEnd: LocalTime = LocalTime.of(7, 0),
    val colorId: Int = 1,
    val type: String = "",
    val roomId: Int = 0
)

fun ClassScheduleDetails.toClass(): ClassSchedule = ClassSchedule(
    id = id,
    title = title,
    section = section,
    teacher = teacher,
    location = location,
    days = days.joinToString(", "),  // Convert list of days to a comma-separated string
    time = time,
    timeEnd = timeEnd,
    roomId = roomId,
    colorId = colorId
)

fun ClassSchedule.toClassScheduleDetails(): ClassScheduleDetails = ClassScheduleDetails(
    id = id,
    title = title,
    section = section,
    teacher = teacher,
    location = location,
    days = days.split(", ").filterNot { it.isEmpty() },  // Convert the string back to a list
    time = time,
    timeEnd = timeEnd,
    roomId = roomId,
    colorId = colorId
)

fun ClassSchedule.toClassScheduleUiState(isEntryValid: Boolean = false): ClassScheduleUiState = ClassScheduleUiState(
    classScheduleDetails = this.toClassScheduleDetails(),
    isEntryValid = isEntryValid
)