package com.example.classschedule.ui.exam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classschedule.data.ExamSchedule
import com.example.classschedule.data.ExamScheduleRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ExamHomeViewModel(examScheduleRepository: ExamScheduleRepository) : ViewModel() {
    val examHomeUiState: StateFlow<ExamHomeUiState> =
        examScheduleRepository.getAllExamsSchedules().map{ ExamHomeUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = ExamHomeUiState()
            )
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class ExamHomeUiState(val examScheduleList: List<ExamSchedule> = listOf())
