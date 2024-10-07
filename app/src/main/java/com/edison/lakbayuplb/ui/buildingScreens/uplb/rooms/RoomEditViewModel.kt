package com.edison.lakbayuplb.ui.buildingScreens.uplb.rooms

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edison.lakbayuplb.data.building.BuildingRepository
import com.edison.lakbayuplb.ui.buildingScreens.uplb.ClassroomDetails
import com.edison.lakbayuplb.ui.buildingScreens.uplb.toClassroom
import com.edison.lakbayuplb.ui.buildingScreens.uplb.toClassroomUiState
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class RoomEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val buildingRepository: BuildingRepository
): ViewModel() {
    private val classroomId: Int = checkNotNull(savedStateHandle[ClassroomEditDestination.CLASROOMIDARG])

    var classroomUiState by mutableStateOf(
        savedStateHandle.get<ClassroomUiState>("classroomUiState") ?: ClassroomUiState()
    )
        private set


    private fun validateInput(uiState: ClassroomDetails = classroomUiState.classroomDetails): Boolean{
        val validTypes = listOf(
            "Room",	"Gallery", "Office", "Library","Institute",	"Department"
        )

        return with(uiState){
            title.isNotBlank() && abbreviation.isNotBlank() && floor.isNotBlank() && validTypes.contains(type)
        }
    }

    init{
        viewModelScope.launch {
            val classroom = buildingRepository.getRoom(classroomId)
                .filterNotNull()
                .first()
            classroomUiState = classroom.toClassroomUiState(true)
        }
    }

    fun updateUiState(classroomDetails: ClassroomDetails){
        classroomUiState = ClassroomUiState(
            classroomDetails = classroomDetails,
            isEntryValid = validateInput(classroomDetails)
        )
    }

    suspend fun updateClassroom(){
        if(validateInput()){
            buildingRepository.update(classroomUiState.classroomDetails.toClassroom())
        }
    }
}
