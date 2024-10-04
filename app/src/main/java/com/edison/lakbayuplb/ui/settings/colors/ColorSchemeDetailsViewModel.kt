package com.edison.lakbayuplb.ui.settings.colors

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edison.lakbayuplb.data.ColorSchemesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ColorSchemeDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val colorSchemesRepository: ColorSchemesRepository
): ViewModel() {
    private val colorId: Int = checkNotNull(savedStateHandle[ColorSchemeDetailsDestination.COLORIDARG])
    val uiState: StateFlow<ColorSchemeDetailsUiState> =
        colorSchemesRepository.getColorSchemeById(colorId)
            .filterNotNull()
            .map{
                ColorSchemeDetailsUiState(colorSchemeDetails = it.toColorSchemeDetails())
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = ColorSchemeDetailsUiState()
            )

    suspend fun deleteColorScheme() {
        colorSchemesRepository.deleteColorScheme(uiState.value.colorSchemeDetails.toColor())
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }


}
data class ColorSchemeDetailsUiState(
    val colorSchemeDetails: ColorSchemeDetails = ColorSchemeDetails()
)