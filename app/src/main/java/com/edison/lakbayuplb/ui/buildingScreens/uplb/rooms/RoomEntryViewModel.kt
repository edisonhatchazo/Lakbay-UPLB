package com.edison.lakbayuplb.ui.buildingScreens.uplb.rooms

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edison.lakbayuplb.data.building.BuildingRepository
import com.edison.lakbayuplb.ui.buildingScreens.uplb.ClassroomDetails
import com.edison.lakbayuplb.ui.buildingScreens.uplb.buildings.BuildingDetailsUiState
import com.edison.lakbayuplb.ui.buildingScreens.uplb.toBuildingDetails
import com.edison.lakbayuplb.ui.buildingScreens.uplb.toClassroom
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class RoomEntryViewModel(
    savedStateHandle: SavedStateHandle,
    private val buildingRepository: BuildingRepository
): ViewModel() {
    private val buildingId: Int = checkNotNull(savedStateHandle[ClassroomEntryDestination.BUILDINGIDARG])

    val buildingUiState: StateFlow<BuildingDetailsUiState> =
        buildingRepository.getBuilding(buildingId)
            .filterNotNull()
            .map {
                BuildingDetailsUiState(buildingDetails = it.toBuildingDetails())
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = BuildingDetailsUiState()
            )

    var classroomUiState by mutableStateOf(
        ClassroomUiState(
            classroomDetails = ClassroomDetails(),
            isEntryValid = false
        )
    )
        private set

    fun updateUiState(classroomDetails: ClassroomDetails){
        classroomUiState = ClassroomUiState(
            classroomDetails = classroomDetails,
            isEntryValid = validateInput(classroomDetails)
        )
    }
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
    private fun validateInput(uiState: ClassroomDetails = classroomUiState.classroomDetails): Boolean{
        val validTypes = listOf(
            "Room",	"Gallery", "Office", "Library","Institute",	"Department"
        )

        return with(uiState){
            title.isNotBlank() && abbreviation.isNotBlank() && floor.isNotBlank() && validTypes.contains(type)
        }
    }

    suspend fun saveClassroom(){
        if(validateInput()){
            buildingRepository.insert(classroomUiState.classroomDetails.toClassroom())
            buildingRepository.incrementRoomCount(buildingId)
        }
    }


}

data class ClassroomUiState(
    val classroomDetails: ClassroomDetails = ClassroomDetails(),
    val isEntryValid: Boolean = false
)