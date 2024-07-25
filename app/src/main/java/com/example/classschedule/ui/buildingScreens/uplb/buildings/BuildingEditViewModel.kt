package com.example.classschedule.ui.buildingScreens.uplb.buildings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classschedule.data.BuildingRepository
import com.example.classschedule.ui.buildingScreens.uplb.BuildingDetails
import com.example.classschedule.ui.buildingScreens.uplb.toBuilding
import com.example.classschedule.ui.buildingScreens.uplb.toBuildingUiState
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BuildingEditViewModel (
    savedStateHandle: SavedStateHandle,
    private val buildingRepository: BuildingRepository
): ViewModel(){
    private val buildingId: Int = checkNotNull(savedStateHandle[BuildingEditDestination.BUILDINGIDARG])
    var buildingUiState by mutableStateOf(
        savedStateHandle.get<BuildingUiState>("buildingUiState") ?: BuildingUiState()
    )
        private set

    init{
        viewModelScope.launch {
            val building = buildingRepository.getBuilding(buildingId)
                .filterNotNull()
                .first()
            buildingUiState = building.toBuildingUiState(true)
        }
    }

    private fun validateInput(uiState: BuildingDetails = buildingUiState.buildingDetails): Boolean {
        val validColleges = listOf(
            "College of Arts and Sciences",
            "College of Development Communication",
            "College of Agriculture",
            "College of Veterinary Medicine",
            "College of Human Ecology",
            "College of Public Affairs and Development",
            "College of Forestry and Natural Resources",
            "College of Economics and Management",
            "College of Engineering and Agro-industrial Technology",
            "Graduate School",
            "UP Unit",
            "Dormitory",
            "Landmark"
        )
        return with(uiState) {
            name.isNotBlank() &&  abbreviation.isNotBlank() && validColleges.contains(college)
        }
    }

    fun updateUiState(buildingDetails: BuildingDetails) {
        buildingUiState = BuildingUiState(
            buildingDetails = buildingDetails,
            isEntryValid = validateInput(buildingDetails)
        )
    }

    suspend fun updateBuilding(){
        if(validateInput()){
            buildingRepository.update(buildingUiState.buildingDetails.toBuilding())
        }
    }

}
