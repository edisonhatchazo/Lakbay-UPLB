package com.example.classschedule.ui.buildingScreens.pins

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.classschedule.data.Pins
import com.example.classschedule.data.PinsRepository

class PinsEntryViewModel(private val pinsRepository: PinsRepository): ViewModel() {
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

    suspend fun savePin(){
        if(validateInput()){
            pinsRepository.insertPin(pinsUiState.pinsDetails.toPins())
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

