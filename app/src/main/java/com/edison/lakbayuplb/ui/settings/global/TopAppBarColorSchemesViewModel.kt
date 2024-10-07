package com.edison.lakbayuplb.ui.settings.global

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edison.lakbayuplb.data.colorschemes.ColorSchemes
import com.edison.lakbayuplb.data.colorschemes.ColorSchemesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TopAppBarColorSchemesViewModel(
    private val appPreferences: AppPreferences,
    private val colorSchemesRepository: ColorSchemesRepository
) : ViewModel() {

    private var _previousColorId: Int = appPreferences.getPreviousColorId() // Retrieve from preferences
    val previousColorId: Int
        get() = _previousColorId

    private val _topAppBarColors = MutableStateFlow(Pair(Color.Blue, Color.White)) // Default colors
    val topAppBarColors: StateFlow<Pair<Color, Color>> = _topAppBarColors.asStateFlow()

    val existingColors: StateFlow<List<ColorSchemes>> = colorSchemesRepository.getAllColorSchemes()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        viewModelScope.launch {
            val savedColors = appPreferences.getTopAppBarColors()
            _topAppBarColors.value = Pair(Color(savedColors.first.toArgb()), Color(savedColors.second.toArgb()))
        }
    }

    private fun updateTopAppBarColors(colorId: Int) {
        viewModelScope.launch {
            colorSchemesRepository.getColorSchemeById(colorId).collect { newScheme ->
                newScheme?.let {
                    val newBackgroundColor = Color(it.backgroundColor)
                    val newForegroundColor = Color(it.fontColor)
                    _topAppBarColors.value = Pair(newBackgroundColor, newForegroundColor)

                    // Save to preferences
                    appPreferences.saveTopAppBarColors(newBackgroundColor, newForegroundColor)
                    appPreferences.savePreviousColorId(colorId) // Save the new color ID as previous

                    // Update the in-memory previous color ID
                    _previousColorId = colorId
                }
            }
        }
    }

    suspend fun updateColor(previousColorId: Int, newColorId: Int) {
        // Save the new color and update the previous color ID
        colorSchemesRepository.decrementIsCurrentlyUsed(previousColorId)
        colorSchemesRepository.incrementIsCurrentlyUsed(newColorId)
        updateTopAppBarColors(newColorId)
    }
}
