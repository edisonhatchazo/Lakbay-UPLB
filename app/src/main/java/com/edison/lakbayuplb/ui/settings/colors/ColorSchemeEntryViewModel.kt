package com.edison.lakbayuplb.ui.settings.colors

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.edison.lakbayuplb.data.ColorSchemes
import com.edison.lakbayuplb.data.ColorSchemesRepository

class ColorSchemeEntryViewModel(private val colorSchemesRepository: ColorSchemesRepository): ViewModel() {
    var entryColorSchemeUiState by mutableStateOf(ColorSchemesUiState())
        private set

    fun updateUiState(colorSchemeDetails: ColorSchemeDetails){
        entryColorSchemeUiState = ColorSchemesUiState(
            colorSchemeDetails = colorSchemeDetails,
            isEntryValid = validateInput(colorSchemeDetails)
        )
    }

    private fun validateInput(uiState: ColorSchemeDetails = entryColorSchemeUiState.colorSchemeDetails): Boolean{
        return with(uiState){
            name.isNotBlank()
        }
    }

    suspend fun saveColorScheme() {
        if (validateInput()) {
            colorSchemesRepository.insertColorScheme(entryColorSchemeUiState.colorSchemeDetails.toColor())
        }
    }

}

data class ColorSchemesUiState(
    val colorSchemeDetails: ColorSchemeDetails = ColorSchemeDetails(),
    val isEntryValid: Boolean = false
)


data class ColorSchemeDetails(
    val id: Int = 0,
    val name: String = "Name",
    val backgroundColor: Int = 0xFF000000.toInt(),
    val fontColor: Int  = 0xFFFFFFFF.toInt(),
    val isCurrentlyUsed: Int = 0
)

fun ColorSchemeDetails.toColor(): ColorSchemes = ColorSchemes(
    id = id,
    name = name,
    backgroundColor = backgroundColor,
    fontColor = fontColor,
    isCurrentlyUsed = isCurrentlyUsed
)

fun ColorSchemes.toColorSchemeDetails(): ColorSchemeDetails = ColorSchemeDetails(
    id = id,
    name = name,
    backgroundColor = backgroundColor,
    fontColor = fontColor,
    isCurrentlyUsed = isCurrentlyUsed
)


fun ColorSchemes.toColorSchemeUiState(): ColorSchemesUiState = ColorSchemesUiState(
    colorSchemeDetails = this.toColorSchemeDetails(),
)