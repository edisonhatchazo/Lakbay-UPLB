package com.example.classschedule.ui.buildingScreens.uplb

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classschedule.data.BuildingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class RoomDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    buildingRepository: BuildingRepository
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


    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class ClassroomDetailsUiState(
    val classroomDetails: ClassroomDetails = ClassroomDetails(),
)
