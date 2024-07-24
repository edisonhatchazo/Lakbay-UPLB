package com.example.classschedule.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classschedule.data.ColorSchemes
import com.example.classschedule.data.ColorSchemesRepository
import kotlinx.coroutines.launch

data class ColorEntry(
    val backgroundColor: Color,
    val fontColor: Color
)

object ColorPalette {
    val colors = mutableMapOf<Int, ColorEntry>()


    fun getColorEntry(colorId: Int): ColorEntry {
        return colors[colorId] ?: ColorEntry(Color.Transparent, Color.Black)
    }
    fun getColorId(colorEntry: ColorEntry): Int? {
        return colors.entries.find { it.value == colorEntry }?.key
    }

}

object CollegeColorPalette {
    val colors = mutableMapOf(
        "College of Arts and Sciences" to Pair("CAS", ColorPalette.getColorEntry(9)),
        "College of Development Communication" to Pair("CDC", ColorPalette.getColorEntry(20)),
        "College of Agriculture" to Pair("CA", ColorPalette.getColorEntry(11)),
        "College of Veterinary Medicine" to Pair("CVM", ColorPalette.getColorEntry(26)),
        "College of Human Ecology" to Pair("CHE", ColorPalette.getColorEntry(8)),
        "College of Public Affairs and Development" to Pair("CPAf", ColorPalette.getColorEntry(24)),
        "College of Forestry and Natural Resources" to Pair("CFNR", ColorPalette.getColorEntry(2)),
        "College of Economics and Management" to Pair("CEM", ColorPalette.getColorEntry(17)),
        "College of Engineering and Agro-industrial Technology" to Pair("CEAT", ColorPalette.getColorEntry(16)),
        "Graduate School" to Pair("GS", ColorPalette.getColorEntry(1)),
        "UP Unit" to Pair("UP Unit", ColorPalette.getColorEntry(22)),
        "Dormitory" to Pair("Dormitory", ColorPalette.getColorEntry(18)),
        "Landmark" to Pair("Landmark", ColorPalette.getColorEntry(12))
    )

    fun getColorEntry(college: String): ColorEntry {
        return colors[college]?.second ?: ColorEntry(Color.Transparent, Color.Black)
    }

    fun updateColorEntry(college: String, newColorId: Int) {
        colors[college] = colors[college]?.copy(second = ColorPalette.getColorEntry(newColorId))
            ?: Pair(college, ColorPalette.getColorEntry(newColorId))
    }


    fun getPreviousColorId(college: String): Int? {
        return colors[college]?.second?.let { ColorPalette.getColorId(it) }
    }
}


class ColorPaletteViewModel(
    private val colorSchemesRepository: ColorSchemesRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            observeColorSchemes()
        }
    }

    suspend fun observeColorSchemes() {
        colorSchemesRepository.getAllColorSchemes().collect { colorSchemes ->
            updateColorPalette(colorSchemes)
        }
    }

    private fun updateColorPalette(colorSchemes: List<ColorSchemes>) {
        // Clear existing entries to avoid stale data
        ColorPalette.colors.clear()

        // Populate with new/updated entries
        colorSchemes.forEach { colorScheme ->
            val colorEntry = ColorEntry(
                backgroundColor = Color(colorScheme.backgroundColor),
                fontColor = Color(colorScheme.fontColor)
            )
            ColorPalette.colors[colorScheme.id] = colorEntry
        }
    }
}