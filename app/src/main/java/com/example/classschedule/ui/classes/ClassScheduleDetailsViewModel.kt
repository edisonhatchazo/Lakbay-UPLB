package com.example.classschedule.ui.classes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classschedule.data.ClassScheduleRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ClassScheduleDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val classScheduleRepository: ClassScheduleRepository
) : ViewModel() {
    private val classScheduleId: Int = checkNotNull(savedStateHandle[ClassScheduleDetailsDestination.CLASSSCHEDULEIDARG])
    val uiState: StateFlow<ClassScheduleDetailsUiState> =
        classScheduleRepository.getClassSchedule(classScheduleId)
            .filterNotNull()
            .map {
                ClassScheduleDetailsUiState(classScheduleDetails = it.toClassScheduleDetails())
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = ClassScheduleDetailsUiState()
            )

    suspend fun deleteClassSchedule() {
        classScheduleRepository.deleteClassSchedule(uiState.value.classScheduleDetails.toClass())
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class ClassScheduleDetailsUiState(
    val classScheduleDetails: ClassScheduleDetails = ClassScheduleDetails()
)