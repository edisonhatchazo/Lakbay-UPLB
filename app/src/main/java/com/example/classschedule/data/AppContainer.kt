package com.example.classschedule.data

import android.content.Context
import com.example.classschedule.algorithm.osrms.OSRMRepository
import com.example.classschedule.algorithm.transit.BusRoute
import com.example.classschedule.algorithm.transit.loadBusRoutes

interface AppContainer {
    val classScheduleRepository: ClassScheduleRepository
    val examScheduleRepository: ExamScheduleRepository
    val pinsRepository: PinsRepository
    val buildingRepository: BuildingRepository
    val osrmRepository: OSRMRepository
    val mapDataRepository: MapDataRepository
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

    override val mapDataRepository: MapDataRepository by lazy{
        val db = MapDatabase.getDatabase(context)
        OfflineMapDataRepository(db.MapDataDao())
    }


    private fun loadAllBusRoutes(context: Context): List<BusRoute> {
        val kananRoutes = loadBusRoutes(context, "Kanan.geojson")
        val kaliwaRoutes = loadBusRoutes(context, "Kaliwa.geojson")
        val forestryRoutes = loadBusRoutes(context, "Forestry_Stops.geojson")
        return kananRoutes + kaliwaRoutes + forestryRoutes
    }

    override val osrmRepository: OSRMRepository by lazy {
        val busRoutes = loadAllBusRoutes(context)
        OSRMRepository(busRoutes)
    }
}