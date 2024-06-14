package com.example.classschedule.ui.classes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classschedule.data.ClassSchedule
import com.example.classschedule.data.ClassScheduleRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ClassHomeViewModel(classScheduleRepository: ClassScheduleRepository) : ViewModel() {
    val classHomeUiState: StateFlow<ClassHomeUiState> =
        classScheduleRepository.getAllClassSchedules().map{ClassHomeUiState(it)}
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = ClassHomeUiState()
            )
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class ClassHomeUiState(val classScheduleList: List<ClassSchedule> = listOf())