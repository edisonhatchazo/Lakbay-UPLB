package com.edison.lakbayuplb.data

import kotlinx.coroutines.flow.Flow

class OfflineClassScheduleRepository(private val classScheduleDao: ClassScheduleDao): ClassScheduleRepository {
    override fun getAllClassSchedules(): Flow<List<com.edison.lakbayuplb.data.ClassSchedule>> = classScheduleDao.getAllClassSchedule()

    override fun getClassSchedule(id: Int): Flow<com.edison.lakbayuplb.data.ClassSchedule?> = classScheduleDao.getClassSchedule(id)

    override fun getLocation(id: Int): Flow<com.edison.lakbayuplb.data.ClassSchedule?> = classScheduleDao.getLocation(id)

    override suspend fun insertClassSchedule(classSchedule: com.edison.lakbayuplb.data.ClassSchedule) = classScheduleDao.insert(classSchedule)

    override suspend fun updateClassSchedule(classSchedule: com.edison.lakbayuplb.data.ClassSchedule) = classScheduleDao.update(classSchedule)

    override suspend fun deleteClassSchedule(classSchedule: com.edison.lakbayuplb.data.ClassSchedule) = classScheduleDao.delete(classSchedule)
}