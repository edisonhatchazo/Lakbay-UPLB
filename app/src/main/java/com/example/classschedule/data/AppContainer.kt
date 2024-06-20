package com.example.classschedule.data

import android.content.Context

interface AppContainer {
    val classScheduleRepository: ClassScheduleRepository
    val examScheduleRepository: ExamScheduleRepository
    val pinsRepository:PinsRepository
}

class AppDataContainer(private val context: Context): AppContainer{

    override val classScheduleRepository: ClassScheduleRepository by lazy{
        OfflineClassScheduleRepository(ClassScheduleDatabase.getDatabase(context).ClassScheduleDao())
    }

    override val examScheduleRepository: ExamScheduleRepository by lazy{
        OfflineExamScheduleRepository(ExamScheduleDatabase.getDatabase(context).ExamScheduleDao())
    }

    override val pinsRepository: PinsRepository by lazy{
        OfflinePinsRepository(PinsDatabase.getDatabase(context).PinsDao())
    }

}