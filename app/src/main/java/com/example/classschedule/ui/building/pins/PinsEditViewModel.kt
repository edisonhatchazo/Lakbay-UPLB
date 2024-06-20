package com.example.classschedule.ui.building.pins

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classschedule.data.PinsRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class PinsEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val pinsRepository: PinsRepository
) : ViewModel() {

    private val pinsId: Int = checkNotNull(savedStateHandle[PinsEditDestination.PINSIDARG])

    var pinsUiState by mutableStateOf(
        savedStateHandle.get<PinsUiState>("pinsUiState") ?: PinsUiState()
    )
        private set

    init {
        viewModelScope.launch{
            val pin = pinsRepository.getPin(pinsId)
                .filterNotNull()
                .first()
            pinsUiState = pin.toPinsUiState(true)
        }
    }

    fun updateUiState(pinsDetails: PinsDetails) {
        pinsUiState = PinsUiState(
            pinsDetails = pinsDetails,
            isEntryValid = validateInput(pinsDetails)
        )
    }
    private fun validateInput(uiState: PinsDetails = pinsUiState.pinsDetails): Boolean{
        return with(uiState){
            title.isNotBlank() && floor.isNotBlank()
        }
    }

    suspend fun updatePin() {
        if (validateInput()) {
            pinsRepository.updatePin(pinsUiState.pinsDetails.toPins())
        }
    }
}