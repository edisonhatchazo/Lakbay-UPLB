package com.edison.lakbayuplb.ui.buildingScreens.pins

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edison.lakbayuplb.data.colorschemes.ColorSchemesRepository
import com.edison.lakbayuplb.data.building.Pins
import com.edison.lakbayuplb.data.building.PinsRepository
import com.edison.lakbayuplb.ui.theme.ColorEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PinsViewModel(
    pinsRepository: PinsRepository,
    private val colorSchemesRepository: ColorSchemesRepository
): ViewModel() {
    private val _colorSchemes = MutableStateFlow<Map<Int, ColorEntry>>(emptyMap())
    val colorSchemes: StateFlow<Map<Int, ColorEntry>> get() = _colorSchemes
    init {
        viewModelScope.launch {
            colorSchemesRepository.getAllColorSchemes().collect { colorSchemes ->
                _colorSchemes.value = colorSchemes.associate { colorScheme ->
                    colorScheme.id to ColorEntry(
                        backgroundColor = Color(colorScheme.backgroundColor),
                        fontColor = Color(colorScheme.fontColor)
                    )
                }
            }
        }
    }

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
