package com.edison.lakbayuplb.ui.navigation

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.edison.lakbayuplb.LakbayUPLBApplication
import com.edison.lakbayuplb.algorithm.SearchViewModel
import com.edison.lakbayuplb.ui.buildingScreens.pins.PinsDetailsViewModel
import com.edison.lakbayuplb.ui.buildingScreens.pins.PinsEditViewModel
import com.edison.lakbayuplb.ui.buildingScreens.pins.PinsEntryViewModel
import com.edison.lakbayuplb.ui.buildingScreens.pins.PinsViewModel
import com.edison.lakbayuplb.ui.buildingScreens.uplb.buildings.BuildingDetailsViewModel
import com.edison.lakbayuplb.ui.buildingScreens.uplb.buildings.BuildingEditViewModel
import com.edison.lakbayuplb.ui.buildingScreens.uplb.buildings.BuildingEntryViewModel
import com.edison.lakbayuplb.ui.buildingScreens.uplb.buildings.BuildingHomeViewModel
import com.edison.lakbayuplb.ui.buildingScreens.uplb.rooms.RoomDetailsViewModel
import com.edison.lakbayuplb.ui.buildingScreens.uplb.rooms.RoomEditViewModel
import com.edison.lakbayuplb.ui.buildingScreens.uplb.rooms.RoomEntryViewModel
import com.edison.lakbayuplb.ui.classes.ClassHomeViewModel
import com.edison.lakbayuplb.ui.classes.ClassScheduleDetailsViewModel
import com.edison.lakbayuplb.ui.classes.ClassScheduleEditViewModel
import com.edison.lakbayuplb.ui.classes.ClassScheduleEntryViewModel
import com.edison.lakbayuplb.ui.exam.ExamDetailsViewModel
import com.edison.lakbayuplb.ui.exam.ExamEditViewModel
import com.edison.lakbayuplb.ui.exam.ExamEntryViewModel
import com.edison.lakbayuplb.ui.exam.ExamHomeViewModel
import com.edison.lakbayuplb.ui.map.LocationViewModel
import com.edison.lakbayuplb.ui.map.MapViewModel
import com.edison.lakbayuplb.ui.settings.colors.ColorSchemeDetailsViewModel
import com.edison.lakbayuplb.ui.settings.colors.ColorSchemeEditViewModel
import com.edison.lakbayuplb.ui.settings.colors.ColorSchemeEntryViewModel
import com.edison.lakbayuplb.ui.settings.colors.ColorSchemeHomeViewModel
import com.edison.lakbayuplb.ui.settings.global.AppPreferences
import com.edison.lakbayuplb.ui.settings.global.CollegeDirectoryViewModel
import com.edison.lakbayuplb.ui.settings.global.DirectoryColorViewModel
import com.edison.lakbayuplb.ui.settings.global.RouteSettingsViewModel
import com.edison.lakbayuplb.ui.settings.global.TopAppBarColorSchemesViewModel
import com.edison.lakbayuplb.ui.theme.ColorPaletteViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory{
        //Initializers for the Class View Models
        initializer{
            ClassHomeViewModel(
                lakbayUPLBApplication().container.classScheduleRepository,
                lakbayUPLBApplication().container.colorSchemesRepository,
            )

        }

        initializer{
            ClassScheduleEditViewModel(
                this.createSavedStateHandle(),
                lakbayUPLBApplication().container.classScheduleRepository,
                lakbayUPLBApplication().container.colorSchemesRepository
            )
        }
        initializer{
            ClassScheduleDetailsViewModel(
                this.createSavedStateHandle(),
                lakbayUPLBApplication().container.classScheduleRepository,
                lakbayUPLBApplication().container.buildingRepository,
                lakbayUPLBApplication().container.mapDataRepository,
                lakbayUPLBApplication().container.colorSchemesRepository,
            )
        }
        initializer{
            ClassScheduleEntryViewModel(
                this.createSavedStateHandle(),
                lakbayUPLBApplication().container.classScheduleRepository,
                lakbayUPLBApplication().container.colorSchemesRepository,
            )
        }

        //Initializers for Exam View Models
        initializer{
            ExamHomeViewModel(
                lakbayUPLBApplication().container.examScheduleRepository,
                lakbayUPLBApplication().container.colorSchemesRepository,
            )
        }


        initializer{
            ExamEditViewModel(
                this.createSavedStateHandle(),
                lakbayUPLBApplication().container.examScheduleRepository,
                lakbayUPLBApplication().container.colorSchemesRepository,
            )
        }

        initializer{
            ExamDetailsViewModel(
                this.createSavedStateHandle(),
                lakbayUPLBApplication().container.examScheduleRepository,
                lakbayUPLBApplication().container.buildingRepository,
                lakbayUPLBApplication().container.mapDataRepository,
                lakbayUPLBApplication().container.colorSchemesRepository,
            )
        }

        initializer{
            ExamEntryViewModel(
                this.createSavedStateHandle(),
                lakbayUPLBApplication().container.examScheduleRepository,
                lakbayUPLBApplication().container.colorSchemesRepository,
            )
        }

        //Initializers for Pins View Models
        initializer{
            PinsViewModel(
                lakbayUPLBApplication().container.pinsRepository,
                lakbayUPLBApplication().container.colorSchemesRepository,
            )
        }

        initializer{
            PinsEditViewModel(
                this.createSavedStateHandle(),
                lakbayUPLBApplication().container.pinsRepository,
                lakbayUPLBApplication().container.colorSchemesRepository
            )
        }

        initializer{
            PinsDetailsViewModel(
                this.createSavedStateHandle(),
                lakbayUPLBApplication().container.mapDataRepository,
                lakbayUPLBApplication().container.pinsRepository,
                lakbayUPLBApplication().container.colorSchemesRepository
            )
        }

        initializer {
            PinsEntryViewModel(
                this.createSavedStateHandle(),
                lakbayUPLBApplication().container.pinsRepository,
                lakbayUPLBApplication().container.colorSchemesRepository
            )
        }

        //Initializers for the Building ViewModels

        initializer{
            BuildingHomeViewModel(
                lakbayUPLBApplication().container.buildingRepository
                )
        }

        initializer {
            BuildingDetailsViewModel(
                this.createSavedStateHandle(),
                lakbayUPLBApplication().container.buildingRepository,
                lakbayUPLBApplication().container.mapDataRepository,

            )
        }

        initializer{
            BuildingEditViewModel(
                this.createSavedStateHandle(),
                lakbayUPLBApplication().container.buildingRepository
            )
        }

        initializer{
            BuildingEntryViewModel(
                lakbayUPLBApplication().container.buildingRepository,

            )
        }

        //initializers for the Room View Models
        initializer{
            RoomDetailsViewModel(
                this.createSavedStateHandle(),
                lakbayUPLBApplication().container.buildingRepository,
                lakbayUPLBApplication().container.mapDataRepository,

            )
        }

        initializer{
            RoomEntryViewModel(
                this.createSavedStateHandle(),
                lakbayUPLBApplication().container.buildingRepository
            )
        }

        initializer{
            RoomEditViewModel(
                this.createSavedStateHandle(),
                lakbayUPLBApplication().container.buildingRepository
            )
        }

        initializer {
            SearchViewModel(
                lakbayUPLBApplication().container.buildingRepository
            )
        }

        initializer {
            MapViewModel(
                lakbayUPLBApplication().container.localRoutingRepository,
                lakbayUPLBApplication()
            )
        }
        initializer{
            LocationViewModel(
                lakbayUPLBApplication(),
                this.createSavedStateHandle(),
                lakbayUPLBApplication().container.mapDataRepository
            )
        }

        initializer{
            ColorSchemeHomeViewModel(
                lakbayUPLBApplication().container.colorSchemesRepository
            )
        }

        initializer{
            ColorSchemeEntryViewModel(
                lakbayUPLBApplication().container.colorSchemesRepository
            )
        }

        initializer {
            ColorSchemeDetailsViewModel(
                this.createSavedStateHandle(),
                lakbayUPLBApplication().container.colorSchemesRepository
            )
        }
        initializer {
            ColorSchemeEditViewModel(
                this.createSavedStateHandle(),
                lakbayUPLBApplication().container.colorSchemesRepository
            )
        }

        initializer{
            ColorPaletteViewModel(
                lakbayUPLBApplication().container.colorSchemesRepository
            )
        }

        initializer{
            CollegeDirectoryViewModel()
        }

        initializer{
            RouteSettingsViewModel(lakbayUPLBApplication().applicationContext)
        }

        initializer{
            DirectoryColorViewModel(
                this.createSavedStateHandle(),
                lakbayUPLBApplication().container.colorSchemesRepository
            )
        }

        initializer{
            TopAppBarColorSchemesViewModel(
                AppPreferences(lakbayUPLBApplication().applicationContext),
                lakbayUPLBApplication().container.colorSchemesRepository
            )
        }

    }
}

fun CreationExtras.lakbayUPLBApplication(): LakbayUPLBApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as LakbayUPLBApplication)