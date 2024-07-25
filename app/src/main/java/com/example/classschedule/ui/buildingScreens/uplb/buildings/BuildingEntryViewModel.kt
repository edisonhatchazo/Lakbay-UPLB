package com.example.classschedule.ui.buildingScreens.uplb.buildings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.classschedule.data.BuildingRepository
import com.example.classschedule.ui.buildingScreens.uplb.BuildingDetails
import com.example.classschedule.ui.buildingScreens.uplb.toBuilding

class BuildingEntryViewModel(
    private val buildingRepository: BuildingRepository
): ViewModel() {
    var buildingUiState by mutableStateOf(
        BuildingUiState(
            buildingDetails = BuildingDetails(),
            isEntryValid = false
        )
    )
        private set
    fun updateUiState(buildingDetails: BuildingDetails){
        buildingUiState = BuildingUiState(
            buildingDetails = buildingDetails,
            isEntryValid = validateInput(buildingDetails)
        )
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

    suspend fun saveBuilding(){
        if(validateInput()){
            buildingRepository.insert(buildingUiState.buildingDetails.toBuilding())
        }
    }

}


data class BuildingUiState(
    val buildingDetails: BuildingDetails = BuildingDetails(),
    val isEntryValid: Boolean = false
)
