package com.example.classschedule.data

import android.content.Context

interface AppContainer {
    val classScheduleRepository: ClassScheduleRepository
}

class AppDataContainer(private val context: Context): AppContainer{

    override val classScheduleRepository: ClassScheduleRepository by lazy{
        OfflineClassScheduleRepository(ClassScheduleDatabase.getDatabase(context).ClassScheduleDao())
    }

}