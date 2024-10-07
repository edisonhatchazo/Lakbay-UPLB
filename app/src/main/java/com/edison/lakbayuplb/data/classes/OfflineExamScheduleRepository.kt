package com.edison.lakbayuplb.data.classes

import kotlinx.coroutines.flow.Flow

class OfflineExamScheduleRepository(private val examScheduleDao: ExamScheduleDao):
    ExamScheduleRepository {
    override fun getAllExamsSchedules(): Flow<List<ExamSchedule>> = examScheduleDao.getAllExamSchedule()

    override fun getExamSchedule(id: Int): Flow<ExamSchedule?> = examScheduleDao.getExamSchedule(id)

    override fun getLocation(id: Int): Flow<ExamSchedule?> = examScheduleDao.getLocation(id)

    override suspend fun insertExamSchedule(examSchedule: ExamSchedule) = examScheduleDao.insert(examSchedule)

    override suspend fun updateExamSchedule(examSchedule: ExamSchedule) = examScheduleDao.update(examSchedule)

    override suspend fun deleteExamSchedule(examSchedule: ExamSchedule) = examScheduleDao.delete(examSchedule)
}