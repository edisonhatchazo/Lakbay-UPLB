package com.example.classschedule.ui.buildingScreens.uplb.buildings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classschedule.data.BuildingRepository
import com.example.classschedule.data.Classroom
import com.example.classschedule.data.MapData
import com.example.classschedule.data.MapDataRepository
import com.example.classschedule.ui.buildingScreens.uplb.BuildingDetails
import com.example.classschedule.ui.buildingScreens.uplb.toBuilding
import com.example.classschedule.ui.buildingScreens.uplb.toBuildingDetails
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class BuildingDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val buildingRepository: BuildingRepository,
    private val mapDataRepository: MapDataRepository,
): ViewModel() {
    private val buildingId: Int = checkNotNull(savedStateHandle[BuildingDetailsDestination.BUILDINGIDARG])


    val uiState: StateFlow<BuildingDetailsUiState> =
        buildingRepository.getBuilding(buildingId)
            .filterNotNull()
            .map {
                BuildingDetailsUiState(buildingDetails = it.toBuildingDetails())
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = BuildingDetailsUiState()
            )

    val buildingRoomUiState: StateFlow<BuildingRoomUiState> =
        buildingRepository.getRoomsByBuildingId(buildingId).map{ BuildingRoomUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = BuildingRoomUiState()
            )
    suspend fun addOrUpdateMapData(building: BuildingDetails) {
        val mapData = MapData(
            mapId = 0,
            title = building.name,
            latitude = building.latitude,
            longitude = building.longitude,
            snippet = ""
        )
        mapDataRepository.insertOrUpdateMapData(mapData)
    }
    suspend fun deleteBuilding() {
        buildingRepository.delete(uiState.value.buildingDetails.toBuilding())
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}

data class BuildingDetailsUiState(
    val buildingDetails: BuildingDetails = BuildingDetails()
)

data class BuildingRoomUiState (val roomList: List<Classroom> = listOf())
