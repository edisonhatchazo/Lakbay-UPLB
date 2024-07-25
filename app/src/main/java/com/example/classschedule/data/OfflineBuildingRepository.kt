package com.example.classschedule.data

import kotlinx.coroutines.flow.Flow

class OfflineBuildingRepository(
    private val buildingDao: BuildingDao,
    private val classroomDao: ClassroomDao
): BuildingRepository {

    override fun getAllBuildings(): Flow<List<Building>> = buildingDao.getAllBuildings()
    override fun getBuilding(id: Int): Flow<Building> = buildingDao.getBuilding(id)
    override fun searchBuildings(query: String): Flow<List<Building>> = buildingDao.searchBuildings(query)
    override suspend fun insert(building: Building) = buildingDao.insert(building)
    override suspend fun update(building: Building) = buildingDao.update(building)
    override suspend fun delete(building: Building) = buildingDao.delete(building)
    override suspend fun incrementRoomCount(id: Int) = buildingDao.incrementRoomCount(id)
    override suspend fun decrementRoomCount(id: Int) = buildingDao.decrementRoomCount(id)

    override fun getRoom(id: Int): Flow<Classroom> = classroomDao.getRoom(id)
    override fun getRoomsByBuildingId(buildingId: Int): Flow<List<Classroom>> = classroomDao.getRoomsByBuildingId(buildingId)
    override fun searchRooms(query: String): Flow<List<Classroom>> = classroomDao.searchRooms(query)
    override suspend fun insert(classroom: Classroom) = classroomDao.insert(classroom)
    override suspend fun update(classroom: Classroom) = classroomDao.update(classroom)
    override suspend fun delete(classroom: Classroom) = classroomDao.delete(classroom)
}

