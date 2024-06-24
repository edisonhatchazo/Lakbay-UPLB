package com.example.classschedule.ui.buildingScreens.pins

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classschedule.data.Pins
import com.example.classschedule.data.PinsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class PinsViewModel(pinsRepository: PinsRepository): ViewModel() {
    val pinsHomeUiState: StateFlow<PinsHomeUiState> =
        pinsRepository.getAllPins().map{PinsHomeUiState(it)}
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = PinsHomeUiState()
            )
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class PinsHomeUiState(val pinsList: List<Pins> = listOf())
