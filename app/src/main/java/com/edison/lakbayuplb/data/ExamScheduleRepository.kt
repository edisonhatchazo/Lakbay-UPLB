package com.edison.lakbayuplb.data

import kotlinx.coroutines.flow.Flow

interface ExamScheduleRepository {
    fun getAllExamsSchedules(): Flow<List<com.edison.lakbayuplb.data.ExamSchedule>>

    fun getExamSchedule(id: Int): Flow<com.edison.lakbayuplb.data.ExamSchedule?>

    fun getLocation(id: Int): Flow<com.edison.lakbayuplb.data.ExamSchedule?>
    suspend fun insertExamSchedule(examSchedule: com.edison.lakbayuplb.data.ExamSchedule)

    suspend fun deleteExamSchedule(examSchedule: com.edison.lakbayuplb.data.ExamSchedule)

    suspend fun updateExamSchedule(examSchedule: com.edison.lakbayuplb.data.ExamSchedule)
}