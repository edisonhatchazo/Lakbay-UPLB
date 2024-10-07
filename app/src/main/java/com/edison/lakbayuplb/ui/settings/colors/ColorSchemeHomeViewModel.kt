package com.edison.lakbayuplb.ui.settings.colors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edison.lakbayuplb.data.colorschemes.ColorSchemes
import com.edison.lakbayuplb.data.colorschemes.ColorSchemesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ColorSchemeHomeViewModel(colorSchemesRepository: ColorSchemesRepository): ViewModel() {
    val existingColors: StateFlow<List<ColorSchemes>> = colorSchemesRepository.getAllColorSchemes()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}



