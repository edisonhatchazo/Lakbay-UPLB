package com.edison.lakbayuplb.ui.classes

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edison.lakbayuplb.data.BuildingRepository
import com.edison.lakbayuplb.data.ClassScheduleRepository
import com.edison.lakbayuplb.data.ColorSchemesRepository
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

class ClassScheduleDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val classScheduleRepository: ClassScheduleRepository,
    private val classroomRepository: BuildingRepository,
    private val mapDataRepository: MapDataRepository,
    private val colorSchemesRepository: ColorSchemesRepository
) : ViewModel() {
    private val classScheduleId: Int = checkNotNull(savedStateHandle[ClassScheduleDetailsDestination.CLASSSCHEDULEIDARG])
    private val _uiState = MutableStateFlow(ClassScheduleDetailsUiState())
    val uiState: StateFlow<ClassScheduleDetailsUiState> = _uiState.asStateFlow()
    private val _colorSchemes = MutableStateFlow<Map<Int, ColorEntry>>(emptyMap())
    val colorSchemes: StateFlow<Map<Int, ColorEntry>> get() = _colorSchemes
    private var colorId = 0
    init {
        viewModelScope.launch {
            val schedule = classScheduleRepository.getClassSchedule(classScheduleId).filterNotNull().first()
            val room = classroomRepository.getRoom(schedule.roomId).firstOrNull()
            _uiState.value = ClassScheduleDetailsUiState(
                classScheduleDetails = schedule.toClassScheduleDetails(),
                latitude = room?.latitude ?: 0.0,
                longitude = room?.longitude ?: 0.0,
                floor = room?.floor ?:""
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

    suspend fun deleteClassSchedule() {
        classScheduleRepository.deleteClassSchedule(uiState.value.classScheduleDetails.toClass())
        colorSchemesRepository.decrementIsCurrentlyUsed(colorId)
    }

    suspend fun addOrUpdateMapData(classScheduleDetails: ClassScheduleDetailsUiState) {
        val mapData = MapData(
            mapId = 0,
            title = classScheduleDetails.classScheduleDetails.title,
            latitude = classScheduleDetails.latitude,
            longitude = classScheduleDetails.longitude,
            snippet = classScheduleDetails.floor
        )
        mapDataRepository.insertOrUpdateMapData(mapData)
    }

}

data class ClassScheduleDetailsUiState(
    val classScheduleDetails: ClassScheduleDetails = ClassScheduleDetails(),
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val floor: String = ""
)