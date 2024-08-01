package com.example.classschedule.ui.buildingScreens.pins

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.classschedule.data.ColorSchemesRepository
import com.example.classschedule.data.Pins
import com.example.classschedule.data.PinsRepository
import com.example.classschedule.ui.settings.colors.ColorSchemesUiState
import com.example.classschedule.ui.settings.colors.toColorSchemeUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class PinsEntryViewModel(
    savedStateHandle: SavedStateHandle,
    private val pinsRepository: PinsRepository,
    private val colorSchemesRepository: ColorSchemesRepository
): ViewModel() {
    var colorSchemesUiState by mutableStateOf(
        savedStateHandle.get<ColorSchemesUiState>("colorSchemeUiState") ?: ColorSchemesUiState()
    )
    var pinsUiState by mutableStateOf(
        PinsUiState(
            isEntryValid = false
        )
    )
        private set
    private fun validateInput(uiState: PinsDetails = pinsUiState.pinsDetails): Boolean{
        return with(uiState){
            title.isNotBlank() && floor.isNotBlank()
        }
    }

    fun updateUiState(pinsDetails: PinsDetails) {
        pinsUiState = PinsUiState(
            pinsDetails = pinsDetails,
            isEntryValid = validateInput(pinsDetails)
        )
    }
    suspend fun getColor(id: Int): ColorSchemesUiState {
        return withContext(Dispatchers.IO) {
            val colorScheme = colorSchemesRepository.getColorSchemeById(id)
                .filterNotNull()
                .first()
            colorSchemesUiState = colorScheme.toColorSchemeUiState()
            colorSchemesUiState
        }
    }
    suspend fun savePin(){
        if(validateInput()){
            pinsRepository.insertPin(pinsUiState.pinsDetails.toPins())
            colorSchemesRepository.incrementIsCurrentlyUsed(pinsUiState.pinsDetails.colorId)
        }
    }
}

data class PinsUiState(
    val pinsDetails: PinsDetails = PinsDetails(),
    val isEntryValid: Boolean = false
)

data class PinsDetails(
    val id: Int = 0,
    val title: String = "",
    val floor: String = "",
    val latitude: Double = 14.16747822735461,
    val longitude: Double = 121.24338486047947,
    val colorId: Int = 0
)

fun PinsDetails.toPins(): Pins = Pins(
    id = id,
    title = title,
    floor = floor,
    latitude = latitude,
    longitude = longitude,
    colorId = colorId
)

fun Pins.toPinsDetails(): PinsDetails = PinsDetails(
    id = id,
    title = title,
    floor = floor,
    latitude = latitude,
    longitude = longitude,
    colorId = colorId
)

fun Pins.toPinsUiState(isEntryValid: Boolean = false): PinsUiState = PinsUiState(
    pinsDetails = this.toPinsDetails(),
    isEntryValid = isEntryValid
)

