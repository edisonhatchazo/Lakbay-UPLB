package com.edison.lakbayuplb.ui.buildingScreens.uplb.buildings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edison.lakbayuplb.data.building.Building
import com.edison.lakbayuplb.data.building.BuildingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class BuildingHomeViewModel(
    buildingRepository: BuildingRepository,
) : ViewModel() {

    val buildingHomeUiState: StateFlow<BuildingHomeUiState> =
        buildingRepository.getAllBuildings().map{ BuildingHomeUiState(it) }
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

