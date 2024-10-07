package com.edison.lakbayuplb.data.classes

import kotlinx.coroutines.flow.Flow

interface ClassScheduleRepository {
    fun getAllClassSchedules(): Flow<List<ClassSchedule>>

    fun getClassSchedule(id: Int): Flow<ClassSchedule?>

    suspend fun insertClassSchedule(classSchedule: ClassSchedule)

    suspend fun deleteClassSchedule(classSchedule: ClassSchedule)

    suspend fun updateClassSchedule(classSchedule: ClassSchedule)

    fun getLocation(id: Int): Flow<ClassSchedule?>
}