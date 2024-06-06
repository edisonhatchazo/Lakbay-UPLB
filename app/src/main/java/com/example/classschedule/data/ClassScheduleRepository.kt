package com.example.classschedule.data

import kotlinx.coroutines.flow.Flow

interface ClassScheduleRepository {
    fun getAllClassScheduleStream(): Flow<List<ClassSchedule>>

    fun getClassScheduleStream(id: Int): Flow<ClassSchedule?>

    suspend fun insertClassSchedule(classSchedule: ClassSchedule)

    suspend fun deleteClassSchedule(classSchedule: ClassSchedule)

    suspend fun updateClassSchedule(classSchedule: ClassSchedule)
}