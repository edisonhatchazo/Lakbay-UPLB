package com.example.classschedule.ui.buildingScreens.uplb

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classschedule.data.Building
import com.example.classschedule.data.BuildingRepository
import com.example.classschedule.data.ColorSchemesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class BuildingHomeViewModel(
    buildingRepository: BuildingRepository,
    private val colorSchemesRepository: ColorSchemesRepository
) : ViewModel() {

    val buildingHomeUiState: StateFlow<BuildingHomeUiState> =
        buildingRepository.getAllBuildings().map{ BuildingHomeUiState(it)}
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = BuildingHomeUiState()
            )
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class BuildingHomeUiState(val buildingList: List<Building> = listOf())

