package com.example.classschedule.data

import android.content.Context

interface AppContainer {
    val classScheduleRepository: ClassScheduleRepository
    val examScheduleRepository: ExamScheduleRepository
    val pinsRepository: PinsRepository
    val buildingRepository: BuildingRepository
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

    override val buildingRepository: BuildingRepository by lazy{
        val db = BuildingDatabase.getDatabase(context)
        OfflineBuildingRepository(db.buildingDao(), db.classroomDao())
    }

}