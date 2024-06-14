package com.example.classschedule.ui.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classschedule.data.ClassScheduleRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ScheduleDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val classScheduleRepository: ClassScheduleRepository
) : ViewModel() {
    private val classScheduleId: Int = checkNotNull(savedStateHandle[ScheduleDetailsDestination.SCHEDULEIDARG])
    val uiState: StateFlow<ScheduleDetailsUiState> =
        classScheduleRepository.getClassSchedule(classScheduleId)
            .filterNotNull()
            .map {
                ScheduleDetailsUiState(scheduleDetails = it.toScheduleDetails())
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = ScheduleDetailsUiState()
            )

    suspend fun deleteClassSchedule() {
        classScheduleRepository.deleteClassSchedule(uiState.value.scheduleDetails.toClass())
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class ScheduleDetailsUiState(
    val scheduleDetails: ScheduleDetails = ScheduleDetails()
)