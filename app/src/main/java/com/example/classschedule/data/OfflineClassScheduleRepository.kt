package com.example.classschedule.data

import kotlinx.coroutines.flow.Flow

class OfflineClassScheduleRepository(private val classScheduleDao: ClassScheduleDao): ClassScheduleRepository {
    override fun getAllClassScheduleStream(): Flow<List<ClassSchedule>> = classScheduleDao.getAllClassSchedule()

    override fun getClassScheduleStream(id: Int): Flow<ClassSchedule?> = classScheduleDao.getClassSchedule(id)

    override suspend fun insertClassSchedule(classSchedule: ClassSchedule) = classScheduleDao.insert(classSchedule)

    override suspend fun updateClassSchedule(classSchedule: ClassSchedule) = classScheduleDao.update(classSchedule)

    override suspend fun deleteClassSchedule(classSchedule: ClassSchedule) = classScheduleDao.delete(classSchedule)
}