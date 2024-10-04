package com.edison.lakbayuplb.ui.settings.global

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edison.lakbayuplb.data.ColorSchemes
import com.edison.lakbayuplb.data.ColorSchemesRepository
import com.edison.lakbayuplb.ui.theme.CollegeColorPalette
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class DirectoryColorViewModel(
    savedStateHandle: SavedStateHandle,
    private val colorSchemesRepository: ColorSchemesRepository
) : ViewModel() {
    val college: String = checkNotNull(savedStateHandle[DirectoryColorsDestination.COLLEGE_ARG])
    val previousColorId: Int = checkNotNull(savedStateHandle[DirectoryColorsDestination.COLORID_ARG])

    val existingColors: StateFlow<List<ColorSchemes>> = colorSchemesRepository.getAllColorSchemes()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun updateCollegeColor(college: String, colorId: Int) {
        CollegeColorPalette.updateColorEntry(college, colorId)
    }

    suspend fun updateColor(previousColorId: Int, newColorId:Int){
        colorSchemesRepository.decrementIsCurrentlyUsed(previousColorId)
        colorSchemesRepository.incrementIsCurrentlyUsed(newColorId)
    }
}