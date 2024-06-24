package com.example.classschedule.data

import kotlinx.coroutines.flow.Flow

class OfflineBuildingRepository(
    private val buildingDao: BuildingDao,
    private val classroomDao: ClassroomDao
): BuildingRepository {

    override fun getAllBuildings(): Flow<List<Building>> = buildingDao.getAllBuildings()

    override fun getBuilding(id: Int): Flow<Building> = buildingDao.getBuilding(id)

    override fun searchBuildings(query: String): Flow<List<Building>> = buildingDao.searchBuildings(query)

    override fun getRoom(id: Int): Flow<Classroom> = classroomDao.getRoom(id)

    override fun getRoomsByBuildingId(buildingId: Int): Flow<List<Classroom>> = classroomDao.getRoomsByBuildingId(buildingId)

    override fun searchRooms(query: String): Flow<List<Classroom>> = classroomDao.searchRooms(query)
}

