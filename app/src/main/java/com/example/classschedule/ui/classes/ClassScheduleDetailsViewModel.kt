package com.example.classschedule.ui.classes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classschedule.data.BuildingRepository
import com.example.classschedule.data.ClassScheduleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ClassScheduleDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val classScheduleRepository: ClassScheduleRepository,
    private val classroomRepository: BuildingRepository
) : ViewModel() {
    private val classScheduleId: Int = checkNotNull(savedStateHandle[ClassScheduleDetailsDestination.CLASSSCHEDULEIDARG])
    private val _uiState = MutableStateFlow(ClassScheduleDetailsUiState())
    val uiState: StateFlow<ClassScheduleDetailsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val schedule = classScheduleRepository.getClassSchedule(classScheduleId).filterNotNull().first()
            val room = classroomRepository.getRoom(schedule.roomId).firstOrNull()
            _uiState.value = ClassScheduleDetailsUiState(
                classScheduleDetails = schedule.toClassScheduleDetails(),
                latitude = room?.latitude ?: 0.0,
                longitude = room?.longitude ?: 0.0
            )
        }
    }

    suspend fun deleteClassSchedule() {
        classScheduleRepository.deleteClassSchedule(uiState.value.classScheduleDetails.toClass())
    }

}

data class ClassScheduleDetailsUiState(
    val classScheduleDetails: ClassScheduleDetails = ClassScheduleDetails(),
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)