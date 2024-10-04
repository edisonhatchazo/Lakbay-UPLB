package com.edison.lakbayuplb.data

import android.content.Context
import com.edison.lakbayuplb.ui.map.routing_algorithm.LocalRoutingRepository
import com.edison.lakbayuplb.ui.map.routing_algorithm.transit.BusRoute
import com.edison.lakbayuplb.ui.map.routing_algorithm.transit.loadBusRoutes

interface AppContainer {
    val classScheduleRepository: ClassScheduleRepository
    val examScheduleRepository: ExamScheduleRepository
    val pinsRepository: PinsRepository
    val buildingRepository: BuildingRepository
    val localRoutingRepository: LocalRoutingRepository
    val mapDataRepository: MapDataRepository
    val colorSchemesRepository: ColorSchemesRepository
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

    override val colorSchemesRepository: ColorSchemesRepository by lazy{
        val db = ColorSchemesDatabase.getDatabase(context)
        OfflineColorSchemesRepository(db.ColorSchemesDao())
    }

    private fun loadAllBusRoutes(context: Context): List<BusRoute> {
        val kananRoutes = loadBusRoutes(context, "Kanan.geojson")
        val kaliwaRoutes = loadBusRoutes(context, "Kaliwa.geojson")
        val forestryRoutes = loadBusRoutes(context, "Forestry_Stops.geojson")
        return kananRoutes + kaliwaRoutes + forestryRoutes
    }

    override val localRoutingRepository: LocalRoutingRepository by lazy {
        val busRoutes = loadAllBusRoutes(context)
        LocalRoutingRepository(busRoutes)
    }
}