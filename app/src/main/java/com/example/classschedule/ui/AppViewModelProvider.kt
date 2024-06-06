package com.example.classschedule.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.classschedule.ClassScheduleApplication
import com.example.classschedule.ui.classes.ClassScheduleDetailsViewModel
import com.example.classschedule.ui.classes.ClassScheduleEditViewModel
import com.example.classschedule.ui.classes.ClassScheduleEntryViewModel
import com.example.classschedule.ui.classes.ClassHomeViewModel
import com.example.classschedule.ui.home.ScheduleDetailsViewModel
import com.example.classschedule.ui.home.ScheduleEditViewModel
import com.example.classschedule.ui.home.ScheduleEntryViewModel
import com.example.classschedule.ui.home.ScheduleViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory{
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
    }
}

fun CreationExtras.classScheduleApplication(): ClassScheduleApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ClassScheduleApplication)