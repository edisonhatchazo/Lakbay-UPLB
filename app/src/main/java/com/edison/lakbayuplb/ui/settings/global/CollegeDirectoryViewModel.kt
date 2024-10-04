package com.edison.lakbayuplb.ui.settings.global

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edison.lakbayuplb.ui.theme.CollegeColorPalette
import com.edison.lakbayuplb.ui.theme.ColorEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CollegeDirectoryViewModel: ViewModel() {
    private val _collegesWithColors = MutableStateFlow(CollegeColorPalette.colors)
    val collegesWithColors: StateFlow<Map<String, Pair<String, ColorEntry>>> get() = _collegesWithColors

    init {
        // Observe changes to CollegeColorPalette and update state
        viewModelScope.launch {
            snapshotFlow { CollegeColorPalette.colors }
                .collect { _collegesWithColors.value = it }
        }
    }

}


