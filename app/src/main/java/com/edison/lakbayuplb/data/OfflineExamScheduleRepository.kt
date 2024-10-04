package com.edison.lakbayuplb.data

import kotlinx.coroutines.flow.Flow

class OfflineExamScheduleRepository(private val examScheduleDao: ExamScheduleDao): ExamScheduleRepository {
    override fun getAllExamsSchedules(): Flow<List<com.edison.lakbayuplb.data.ExamSchedule>> = examScheduleDao.getAllExamSchedule()

    override fun getExamSchedule(id: Int): Flow<com.edison.lakbayuplb.data.ExamSchedule?> = examScheduleDao.getExamSchedule(id)

    override fun getLocation(id: Int): Flow<com.edison.lakbayuplb.data.ExamSchedule?> = examScheduleDao.getLocation(id)

    override suspend fun insertExamSchedule(examSchedule: com.edison.lakbayuplb.data.ExamSchedule) = examScheduleDao.insert(examSchedule)

    override suspend fun updateExamSchedule(examSchedule: com.edison.lakbayuplb.data.ExamSchedule) = examScheduleDao.update(examSchedule)

    override suspend fun deleteExamSchedule(examSchedule: com.edison.lakbayuplb.data.ExamSchedule) = examScheduleDao.delete(examSchedule)
}