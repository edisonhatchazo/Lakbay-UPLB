package com.example.classschedule.ui.buildingScreens.uplb

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classschedule.data.BuildingRepository
import com.example.classschedule.data.MapData
import com.example.classschedule.data.MapDataRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class RoomDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    buildingRepository: BuildingRepository,
    private val mapDataRepository: MapDataRepository
): ViewModel() {
    private val roomId: Int = checkNotNull(savedStateHandle[RoomDetailsDestination.ROOMIDARG])


    val uiState: StateFlow<ClassroomDetailsUiState> =
        buildingRepository.getRoom(roomId)
            .filterNotNull()
            .map{
                ClassroomDetailsUiState(classroomDetails = it.toClassroomDetails())
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = ClassroomDetailsUiState()
            )

    suspend fun addOrUpdateMapData(room: ClassroomDetails) {
        val mapData = MapData(
            mapId = 0,
            title = room.title,
            latitude = room.latitude,
            longitude = room.longitude,
            snippet = room.floor
        )
        mapDataRepository.insertOrUpdateMapData(mapData)
    }
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }


}

data class ClassroomDetailsUiState(
    val classroomDetails: ClassroomDetails = ClassroomDetails(),
)
