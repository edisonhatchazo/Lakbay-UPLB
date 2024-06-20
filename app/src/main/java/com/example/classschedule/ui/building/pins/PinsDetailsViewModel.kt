package com.example.classschedule.ui.building.pins

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classschedule.data.PinsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


class PinsDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val pinsRepository: PinsRepository
) : ViewModel() {
    private val pinId: Int = checkNotNull(savedStateHandle[PinsDetailsDestination.PINIDARG])
    val uiState: StateFlow<PinsDetailsUiState> =
        pinsRepository.getPin(pinId)
            .filterNotNull()
            .map {
                PinsDetailsUiState(pinsDetails = it.toPinsDetails())
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = PinsDetailsUiState()
            )

    suspend fun deletePin() {
        pinsRepository.deletePin(uiState.value.pinsDetails.toPins())
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class PinsDetailsUiState(
    val pinsDetails: PinsDetails = PinsDetails()
)

