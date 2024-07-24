package com.example.classschedule.ui.settings.colors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classschedule.data.ColorSchemes
import com.example.classschedule.data.ColorSchemesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ColorSchemeHomeViewModel(colorSchemesRepository: ColorSchemesRepository): ViewModel() {
    val existingColors: StateFlow<List<ColorSchemes>> = colorSchemesRepository.getAllColorSchemes()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}



