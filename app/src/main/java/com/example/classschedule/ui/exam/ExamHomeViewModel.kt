package com.example.classschedule.ui.exam

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classschedule.data.ColorSchemesRepository
import com.example.classschedule.data.ExamSchedule
import com.example.classschedule.data.ExamScheduleRepository
import com.example.classschedule.ui.theme.ColorEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExamHomeViewModel(
    examScheduleRepository: ExamScheduleRepository,
    private val colorSchemesRepository: ColorSchemesRepository
) : ViewModel() {
    private val _colorSchemes = MutableStateFlow<Map<Int, ColorEntry>>(emptyMap())
    val colorSchemes: StateFlow<Map<Int, ColorEntry>> get() = _colorSchemes
    init {
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

    fun getColor(colorId: Int): ColorEntry {
        return colorSchemes.value[colorId] ?: ColorEntry(Color.Transparent, Color.Black)
    }
    val examHomeUiState: StateFlow<ExamHomeUiState> =
        examScheduleRepository.getAllExamsSchedules().map{ ExamHomeUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = ExamHomeUiState()
            )
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}

data class ExamHomeUiState(val examScheduleList: List<ExamSchedule> = listOf())
