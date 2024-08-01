package com.example.classschedule.ui.buildingScreens.pins

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classschedule.data.ColorSchemesRepository
import com.example.classschedule.data.MapData
import com.example.classschedule.data.MapDataRepository
import com.example.classschedule.data.PinsRepository
import com.example.classschedule.ui.theme.ColorEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class PinsDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val mapDataRepository: MapDataRepository,
    private val pinsRepository: PinsRepository,
    private val colorSchemesRepository: ColorSchemesRepository
) : ViewModel() {
    private val pinId: Int = checkNotNull(savedStateHandle[PinsDetailsDestination.PINIDARG])
    private val _colorSchemes = MutableStateFlow<Map<Int, ColorEntry>>(emptyMap())
    val colorSchemes: StateFlow<Map<Int, ColorEntry>> get() = _colorSchemes
    private var colorId = 1
    val uiState: StateFlow<PinsDetailsUiState> =
        pinsRepository.getPin(pinId)
            .filterNotNull()
            .map {
                PinsDetailsUiState(pinsDetails = it.toPinsDetails())
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = PinsDetailsUiState()
            )

    init{
        viewModelScope.launch {
            val schedule = pinsRepository.getPin(pinId).filterNotNull().first()
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

    suspend fun deletePin() {
        pinsRepository.deletePin(uiState.value.pinsDetails.toPins())
        colorSchemesRepository.decrementIsCurrentlyUsed(colorId)
    }

    suspend fun addOrUpdateMapData(pin: PinsDetails) {
        val mapData = MapData(
            mapId = 0,
            title = pin.title,
            latitude = pin.latitude,
            longitude = pin.longitude,
            snippet = pin.floor
        )
        mapDataRepository.insertOrUpdateMapData(mapData)
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class PinsDetailsUiState(
    val pinsDetails: PinsDetails = PinsDetails()
)

