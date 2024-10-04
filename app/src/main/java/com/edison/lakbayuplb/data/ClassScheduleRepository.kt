package com.edison.lakbayuplb.data

import kotlinx.coroutines.flow.Flow

interface ClassScheduleRepository {
    fun getAllClassSchedules(): Flow<List<com.edison.lakbayuplb.data.ClassSchedule>>

    fun getClassSchedule(id: Int): Flow<com.edison.lakbayuplb.data.ClassSchedule?>

    suspend fun insertClassSchedule(classSchedule: com.edison.lakbayuplb.data.ClassSchedule)

    suspend fun deleteClassSchedule(classSchedule: com.edison.lakbayuplb.data.ClassSchedule)

    suspend fun updateClassSchedule(classSchedule: com.edison.lakbayuplb.data.ClassSchedule)

    fun getLocation(id: Int): Flow<com.edison.lakbayuplb.data.ClassSchedule?>
}