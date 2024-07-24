package com.example.classschedule.ui.settings.colors

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classschedule.data.ColorSchemesRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ColorSchemeEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val colorSchemesRepository: ColorSchemesRepository
): ViewModel() {
    private val colorId: Int = checkNotNull(savedStateHandle[ColorSchemeEditDestination.COLORIDARG])

    var colorSchemesUiState by mutableStateOf(
        savedStateHandle.get<ColorSchemesUiState>("colorSchemeUiState") ?: ColorSchemesUiState()
    )

        private set
    init{
        viewModelScope.launch {
            val colorScheme = colorSchemesRepository.getColorSchemeById(colorId)
                .filterNotNull()
                .first()
            colorSchemesUiState = colorScheme.toColorSchemeUiState()
        }
    }

    private fun validateInput(uiState: ColorSchemeDetails = colorSchemesUiState.colorSchemeDetails): Boolean{
        return with(uiState){
            name.isNotBlank()
        }
    }

    fun updateUiState(colorSchemeDetails: ColorSchemeDetails){
        colorSchemesUiState = ColorSchemesUiState(
            colorSchemeDetails = colorSchemeDetails,
            isEntryValid = validateInput(colorSchemeDetails)
        )
    }

    suspend fun updateColor(){
        if(validateInput()){
            colorSchemesRepository.updateColorScheme(colorSchemesUiState.colorSchemeDetails.toColor())
        }
    }
}
