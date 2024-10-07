package com.edison.lakbayuplb.data.classes

import kotlinx.coroutines.flow.Flow

class OfflineClassScheduleRepository(private val classScheduleDao: ClassScheduleDao):
    ClassScheduleRepository {
    override fun getAllClassSchedules(): Flow<List<ClassSchedule>> = classScheduleDao.getAllClassSchedule()

    override fun getClassSchedule(id: Int): Flow<ClassSchedule?> = classScheduleDao.getClassSchedule(id)

    override fun getLocation(id: Int): Flow<ClassSchedule?> = classScheduleDao.getLocation(id)

    override suspend fun insertClassSchedule(classSchedule: ClassSchedule) = classScheduleDao.insert(classSchedule)

    override suspend fun updateClassSchedule(classSchedule: ClassSchedule) = classScheduleDao.update(classSchedule)

    override suspend fun deleteClassSchedule(classSchedule: ClassSchedule) = classScheduleDao.delete(classSchedule)
}