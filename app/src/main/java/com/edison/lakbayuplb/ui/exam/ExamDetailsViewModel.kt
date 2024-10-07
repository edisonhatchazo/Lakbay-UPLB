package com.edison.lakbayuplb.ui.exam

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edison.lakbayuplb.data.building.BuildingRepository
import com.edison.lakbayuplb.data.colorschemes.ColorSchemesRepository
import com.edison.lakbayuplb.data.classes.ExamScheduleRepository
import com.edison.lakbayuplb.data.MapData
import com.edison.lakbayuplb.data.MapDataRepository
import com.edison.lakbayuplb.ui.theme.ColorEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ExamDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val examScheduleRepository: ExamScheduleRepository,
    private val classroomRepository: BuildingRepository,
    private val mapDataRepository: MapDataRepository,
    private val colorSchemesRepository: ColorSchemesRepository
) : ViewModel() {
    private val classScheduleId: Int = checkNotNull(savedStateHandle[ExamDetailsDestination.SCHEDULEIDARG])

    private val _uiState = MutableStateFlow(ExamScheduleDetailsUiState())
    val uiState: StateFlow<ExamScheduleDetailsUiState> = _uiState.asStateFlow()

    private val _colorSchemes = MutableStateFlow<Map<Int, ColorEntry>>(emptyMap())
    val colorSchemes: StateFlow<Map<Int, ColorEntry>> get() = _colorSchemes
    private var colorId = 0
    init {
        viewModelScope.launch {
            val schedule = examScheduleRepository.getExamSchedule(classScheduleId).filterNotNull().first()
            val room = classroomRepository.getRoom(schedule.roomId).firstOrNull()
            _uiState.value = ExamScheduleDetailsUiState(
                examScheduleDetails = schedule.toExamScheduleDetails(),
                latitude = room?.latitude ?: 0.0,
                longitude = room?.longitude ?: 0.0,
                floor = room?.floor ?: ""
            )
            colorId = schedule.colorId
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

    suspend fun addOrUpdateMapData(examScheduleDetails: ExamScheduleDetailsUiState) {
        val mapData = MapData(
            mapId = 0,
            title = examScheduleDetails.examScheduleDetails.title,
            latitude = examScheduleDetails.latitude,
            longitude = examScheduleDetails.longitude,
            snippet = examScheduleDetails.floor
        )
        mapDataRepository.insertOrUpdateMapData(mapData)
    }

    suspend fun deleteExamSchedule() {
        examScheduleRepository.deleteExamSchedule(uiState.value.examScheduleDetails.toExam())
        colorSchemesRepository.decrementIsCurrentlyUsed(colorId)
    }


}

data class ExamScheduleDetailsUiState(
    val examScheduleDetails: ExamScheduleDetails = ExamScheduleDetails(),
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val floor: String = ""
)