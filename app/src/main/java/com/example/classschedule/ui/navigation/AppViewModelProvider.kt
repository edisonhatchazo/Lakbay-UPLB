package com.example.classschedule.ui.navigation

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.classschedule.ClassScheduleApplication
import com.example.classschedule.algorithm.SearchViewModel
import com.example.classschedule.ui.buildingScreens.pins.PinsDetailsViewModel
import com.example.classschedule.ui.buildingScreens.pins.PinsEditViewModel
import com.example.classschedule.ui.buildingScreens.pins.PinsEntryViewModel
import com.example.classschedule.ui.buildingScreens.pins.PinsViewModel
import com.example.classschedule.ui.buildingScreens.uplb.buildings.BuildingDetailsViewModel
import com.example.classschedule.ui.buildingScreens.uplb.buildings.BuildingEditViewModel
import com.example.classschedule.ui.buildingScreens.uplb.buildings.BuildingEntryViewModel
import com.example.classschedule.ui.buildingScreens.uplb.buildings.BuildingHomeViewModel
import com.example.classschedule.ui.buildingScreens.uplb.rooms.RoomDetailsViewModel
import com.example.classschedule.ui.buildingScreens.uplb.rooms.RoomEditViewModel
import com.example.classschedule.ui.buildingScreens.uplb.rooms.RoomEntryViewModel
import com.example.classschedule.ui.classes.ClassHomeViewModel
import com.example.classschedule.ui.classes.ClassScheduleDetailsViewModel
import com.example.classschedule.ui.classes.ClassScheduleEditViewModel
import com.example.classschedule.ui.classes.ClassScheduleEntryViewModel
import com.example.classschedule.ui.exam.ExamDetailsViewModel
import com.example.classschedule.ui.exam.ExamEditViewModel
import com.example.classschedule.ui.exam.ExamEntryViewModel
import com.example.classschedule.ui.exam.ExamHomeViewModel
import com.example.classschedule.ui.map.LocationViewModel
import com.example.classschedule.ui.map.MapViewModel
import com.example.classschedule.ui.settings.colors.ColorSchemeDetailsViewModel
import com.example.classschedule.ui.settings.colors.ColorSchemeEditViewModel
import com.example.classschedule.ui.settings.colors.ColorSchemeEntryViewModel
import com.example.classschedule.ui.settings.colors.ColorSchemeHomeViewModel
import com.example.classschedule.ui.settings.global.CollegeDirectoryViewModel
import com.example.classschedule.ui.settings.global.DirectoryColorViewModel
import com.example.classschedule.ui.settings.global.RouteSettingsViewModel
import com.example.classschedule.ui.theme.ColorPaletteViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory{
        //Initializers for the Class View Models
        initializer{
            ClassHomeViewModel(
                classScheduleApplication().container.classScheduleRepository,
                classScheduleApplication().container.colorSchemesRepository,
            )

        }

        initializer{
            ClassScheduleEditViewModel(
                this.createSavedStateHandle(),
                classScheduleApplication().container.classScheduleRepository,
                classScheduleApplication().container.colorSchemesRepository
            )
        }
        initializer{
            ClassScheduleDetailsViewModel(
                this.createSavedStateHandle(),
                classScheduleApplication().container.classScheduleRepository,
                classScheduleApplication().container.buildingRepository,
                classScheduleApplication().container.mapDataRepository,
                classScheduleApplication().container.colorSchemesRepository,
            )
        }
        initializer{
            ClassScheduleEntryViewModel(
                this.createSavedStateHandle(),
                classScheduleApplication().container.classScheduleRepository,
                classScheduleApplication().container.colorSchemesRepository,
            )
        }

        //Initializers for Exam View Models
        initializer{
            ExamHomeViewModel(
                classScheduleApplication().container.examScheduleRepository,
                classScheduleApplication().container.colorSchemesRepository,
            )
        }


        initializer{
            ExamEditViewModel(
                this.createSavedStateHandle(),
                classScheduleApplication().container.examScheduleRepository,
                classScheduleApplication().container.colorSchemesRepository,
            )
        }

        initializer{
            ExamDetailsViewModel(
                this.createSavedStateHandle(),
                classScheduleApplication().container.examScheduleRepository,
                classScheduleApplication().container.buildingRepository,
                classScheduleApplication().container.mapDataRepository,
                classScheduleApplication().container.colorSchemesRepository,
            )
        }

        initializer{
            ExamEntryViewModel(
                this.createSavedStateHandle(),
                classScheduleApplication().container.examScheduleRepository,
                classScheduleApplication().container.colorSchemesRepository,
            )
        }

        //Initializers for Pins View Models
        initializer{
            PinsViewModel(
                classScheduleApplication().container.pinsRepository,
                classScheduleApplication().container.colorSchemesRepository,
            )
        }

        initializer{
            PinsEditViewModel(
                this.createSavedStateHandle(),
                classScheduleApplication().container.pinsRepository,
                classScheduleApplication().container.colorSchemesRepository
            )
        }

        initializer{
            PinsDetailsViewModel(
                this.createSavedStateHandle(),
                classScheduleApplication().container.mapDataRepository,
                classScheduleApplication().container.pinsRepository,
                classScheduleApplication().container.colorSchemesRepository
            )
        }

        initializer {
            PinsEntryViewModel(
                this.createSavedStateHandle(),
                classScheduleApplication().container.pinsRepository,
                classScheduleApplication().container.colorSchemesRepository
            )
        }

        //Initializers for the Building ViewModels

        initializer{
            BuildingHomeViewModel(
                classScheduleApplication().container.buildingRepository
                )
        }

        initializer {
            BuildingDetailsViewModel(
                this.createSavedStateHandle(),
                classScheduleApplication().container.buildingRepository,
                classScheduleApplication().container.mapDataRepository,

            )
        }

        initializer{
            BuildingEditViewModel(
                this.createSavedStateHandle(),
                classScheduleApplication().container.buildingRepository
            )
        }

        initializer{
            BuildingEntryViewModel(
                classScheduleApplication().container.buildingRepository,

            )
        }

        //initializers for the Room View Models
        initializer{
            RoomDetailsViewModel(
                this.createSavedStateHandle(),
                classScheduleApplication().container.buildingRepository,
                classScheduleApplication().container.mapDataRepository,

            )
        }

        initializer{
            RoomEntryViewModel(
                this.createSavedStateHandle(),
                classScheduleApplication().container.buildingRepository
            )
        }

        initializer{
            RoomEditViewModel(
                this.createSavedStateHandle(),
                classScheduleApplication().container.buildingRepository
            )
        }

        initializer {
            SearchViewModel(
                classScheduleApplication().container.buildingRepository
            )
        }

        initializer {
            MapViewModel(
                classScheduleApplication().container.osrmRepository,
                classScheduleApplication()
            )
        }
        initializer{
            LocationViewModel(
                classScheduleApplication(),
                this.createSavedStateHandle(),
                classScheduleApplication().container.mapDataRepository
            )
        }

        initializer{
            ColorSchemeHomeViewModel(
                classScheduleApplication().container.colorSchemesRepository
            )
        }

        initializer{
            ColorSchemeEntryViewModel(
                classScheduleApplication().container.colorSchemesRepository
            )
        }

        initializer {
            ColorSchemeDetailsViewModel(
                this.createSavedStateHandle(),
                classScheduleApplication().container.colorSchemesRepository
            )
        }
        initializer {
            ColorSchemeEditViewModel(
                this.createSavedStateHandle(),
                classScheduleApplication().container.colorSchemesRepository
            )
        }

        initializer{
            ColorPaletteViewModel(
                classScheduleApplication().container.colorSchemesRepository
            )
        }

        initializer{
            CollegeDirectoryViewModel()
        }

        initializer{
            RouteSettingsViewModel(classScheduleApplication().applicationContext)
        }

        initializer{
            DirectoryColorViewModel(
                this.createSavedStateHandle(),
                classScheduleApplication().container.colorSchemesRepository
            )
        }

    }
}

fun CreationExtras.classScheduleApplication(): ClassScheduleApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ClassScheduleApplication)