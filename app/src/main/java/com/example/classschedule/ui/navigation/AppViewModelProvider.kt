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
import com.example.classschedule.ui.buildingScreens.uplb.BuildingDetailsViewModel
import com.example.classschedule.ui.buildingScreens.uplb.BuildingHomeViewModel
import com.example.classschedule.ui.buildingScreens.uplb.RoomDetailsViewModel
import com.example.classschedule.ui.classes.ClassHomeViewModel
import com.example.classschedule.ui.classes.ClassScheduleDetailsViewModel
import com.example.classschedule.ui.classes.ClassScheduleEditViewModel
import com.example.classschedule.ui.classes.ClassScheduleEntryViewModel
import com.example.classschedule.ui.exam.ExamDetailsViewModel
import com.example.classschedule.ui.exam.ExamEditViewModel
import com.example.classschedule.ui.exam.ExamEntryViewModel
import com.example.classschedule.ui.exam.ExamHomeViewModel
import com.example.classschedule.ui.home.ScheduleDetailsViewModel
import com.example.classschedule.ui.home.ScheduleEditViewModel
import com.example.classschedule.ui.home.ScheduleEntryViewModel
import com.example.classschedule.ui.home.ScheduleViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory{
        //Initializers for the Class View Models
        initializer{
            ClassHomeViewModel(classScheduleApplication().container.classScheduleRepository)
        }

        initializer{
            ClassScheduleEditViewModel(
                this.createSavedStateHandle(),
                classScheduleApplication().container.classScheduleRepository
            )
        }
        initializer{
            ClassScheduleDetailsViewModel(
                this.createSavedStateHandle(),
                classScheduleApplication().container.classScheduleRepository
            )
        }
        initializer{
            ClassScheduleEntryViewModel(classScheduleApplication().container.classScheduleRepository)
        }

        //Initializers for Schedule View Models
        initializer {
            ScheduleViewModel(classScheduleApplication().container.classScheduleRepository)
        }
        initializer{
            ScheduleEditViewModel(
                this.createSavedStateHandle(),
                classScheduleApplication().container.classScheduleRepository
            )
        }
        initializer{
            ScheduleDetailsViewModel(
                this.createSavedStateHandle(),
                classScheduleApplication().container.classScheduleRepository
            )
        }
        initializer{
            ScheduleEntryViewModel(classScheduleApplication().container.classScheduleRepository)
        }

        //Initializers for Exam View Models
        initializer{
            ExamHomeViewModel(classScheduleApplication().container.examScheduleRepository)
        }


        initializer{
            ExamEditViewModel(
                this.createSavedStateHandle(),
                classScheduleApplication().container.examScheduleRepository
            )
        }

        initializer{
            ExamDetailsViewModel(
                this.createSavedStateHandle(),
                classScheduleApplication().container.examScheduleRepository
            )
        }

        initializer{
            ExamEntryViewModel(classScheduleApplication().container.examScheduleRepository)
        }

        //Initializers for Pins View Models
        initializer{
            PinsViewModel(classScheduleApplication().container.pinsRepository)
        }

        initializer{
            PinsEditViewModel(
                this.createSavedStateHandle(),
                classScheduleApplication().container.pinsRepository
            )
        }

        initializer{
            PinsDetailsViewModel(
                this.createSavedStateHandle(),
                classScheduleApplication().container.pinsRepository
            )
        }

        initializer {
            PinsEntryViewModel(classScheduleApplication().container.pinsRepository)
        }

        initializer{
            BuildingHomeViewModel(classScheduleApplication().container.buildingRepository)
        }

        initializer {
            BuildingDetailsViewModel(
                this.createSavedStateHandle(),
                classScheduleApplication().container.buildingRepository
            )
        }
        initializer{
            RoomDetailsViewModel(
                this.createSavedStateHandle(),
                classScheduleApplication().container.buildingRepository
            )
        }

        initializer {
            SearchViewModel(
                classScheduleApplication().container.buildingRepository
            )
        }

    }
}

fun CreationExtras.classScheduleApplication(): ClassScheduleApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ClassScheduleApplication)