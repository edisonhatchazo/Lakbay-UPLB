package com.example.classschedule.data

import android.content.Context

interface AppContainer {
    val classScheduleRepository: ClassScheduleRepository
    val examScheduleRepository: ExamScheduleRepository
}

class AppDataContainer(private val context: Context): AppContainer{

    override val classScheduleRepository: ClassScheduleRepository by lazy{
        OfflineClassScheduleRepository(ClassScheduleDatabase.getDatabase(context).ClassScheduleDao())
    }

    override val examScheduleRepository: ExamScheduleRepository by lazy{
        OfflineExamScheduleRepository(ExamScheduleDatabase.getDatabase(context).ExamScheduleDao())
    }


}