package com.example.classschedule.ui.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classschedule.data.BuildingRepository
import com.example.classschedule.data.ClassScheduleRepository
import com.example.classschedule.data.MapData
import com.example.classschedule.data.MapDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ScheduleDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val classScheduleRepository: ClassScheduleRepository,
    private val classroomRepository: BuildingRepository,
    private val mapDataRepository: MapDataRepository,
) : ViewModel() {
    private val classScheduleId: Int = checkNotNull(savedStateHandle[ScheduleDetailsDestination.SCHEDULEIDARG])

    private val _uiState = MutableStateFlow(ScheduleDetailsUiState())
    val uiState: StateFlow<ScheduleDetailsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val schedule = classScheduleRepository.getClassSchedule(classScheduleId).filterNotNull().first()
            val room = classroomRepository.getRoom(schedule.roomId).firstOrNull()
            _uiState.value = ScheduleDetailsUiState(
                scheduleDetails = schedule.toScheduleDetails(),
                latitude = room?.latitude ?: 0.0,
                longitude = room?.longitude ?: 0.0,
                floor = room?.floor ?: ""
            )
        }
    }

    suspend fun addOrUpdateMapData(scheduleDetails: ScheduleDetailsUiState) {
        val mapData = MapData(
            mapId = 0,
            title = scheduleDetails.scheduleDetails.title,
            latitude = scheduleDetails.latitude,
            longitude = scheduleDetails.longitude,
            snippet = scheduleDetails.floor
        )
        mapDataRepository.insertOrUpdateMapData(mapData)
    }

    suspend fun deleteClassSchedule() {
        classScheduleRepository.deleteClassSchedule(uiState.value.scheduleDetails.toClass())
    }

}

data class ScheduleDetailsUiState(
    val scheduleDetails: ScheduleDetails = ScheduleDetails(),
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val floor: String = ""
)