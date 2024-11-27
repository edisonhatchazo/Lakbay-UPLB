package com.edison.lakbayuplb.ui.classes

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edison.lakbayuplb.algorithm.routing_algorithm.LocalRoutingRepository
import com.edison.lakbayuplb.data.classes.ClassSchedule
import com.edison.lakbayuplb.data.classes.ClassScheduleRepository
import com.edison.lakbayuplb.data.colorschemes.ColorSchemesRepository
import com.edison.lakbayuplb.ui.theme.ColorEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ClassHomeViewModel(
    classScheduleRepository: ClassScheduleRepository,
    private val colorSchemesRepository: ColorSchemesRepository,
    private val localRoutingRepository: LocalRoutingRepository,
    application: Application
) : ViewModel() {
    private val _colorSchemes = MutableStateFlow<Map<Int, ColorEntry>>(emptyMap())
    val colorSchemes: StateFlow<Map<Int, ColorEntry>> get() = _colorSchemes
    init {
        localRoutingRepository.initializeGraphs(context = application.baseContext)
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

    val classHomeUiState: StateFlow<ClassHomeUiState> =
        classScheduleRepository.getAllClassSchedules().map{ ClassHomeUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = ClassHomeUiState()
            )
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class ClassHomeUiState(val classScheduleList: List<ClassSchedule> = listOf())