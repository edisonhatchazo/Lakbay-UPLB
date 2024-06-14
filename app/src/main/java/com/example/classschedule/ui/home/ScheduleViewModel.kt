package com.example.classschedule.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classschedule.data.ClassSchedule
import com.example.classschedule.data.ClassScheduleRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


class ScheduleViewModel(classScheduleRepository: ClassScheduleRepository) : ViewModel() {
    val classHomeUiState: StateFlow<SchedulesUiState> =
        classScheduleRepository.getAllClassSchedules().map{SchedulesUiState(it)}
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = SchedulesUiState()
            )
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class SchedulesUiState(val classScheduleList: List<ClassSchedule> = listOf())