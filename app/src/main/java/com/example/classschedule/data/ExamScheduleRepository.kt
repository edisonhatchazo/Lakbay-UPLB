package com.example.classschedule.data

import kotlinx.coroutines.flow.Flow

interface ExamScheduleRepository {
    fun getAllExamsSchedules(): Flow<List<ExamSchedule>>

    fun getExamSchedule(id: Int): Flow<ExamSchedule?>

    fun getLocation(id: Int): Flow<ExamSchedule?>
    suspend fun insertExamSchedule(examSchedule: ExamSchedule)

    suspend fun deleteExamSchedule(examSchedule: ExamSchedule)

    suspend fun updateExamSchedule(examSchedule: ExamSchedule)
}