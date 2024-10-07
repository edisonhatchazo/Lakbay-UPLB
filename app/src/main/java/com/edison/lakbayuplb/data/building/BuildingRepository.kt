package com.edison.lakbayuplb.data.building

import androidx.room.Delete
import androidx.room.Update
import com.edison.lakbayuplb.data.building.Building
import com.edison.lakbayuplb.data.building.Classroom
import kotlinx.coroutines.flow.Flow

interface BuildingRepository {
    fun getAllBuildings(): Flow<List<Building>>

    fun searchBuildings(query: String): Flow<List<Building>>

    fun getBuilding(id: Int): Flow<Building>

    fun searchRooms(query: String): Flow<List<Classroom>>

    fun getRoom(id: Int): Flow<Classroom>

    fun getRoomsByBuildingId(buildingId: Int): Flow<List<Classroom>>

    suspend fun insert(classroom: Classroom)

    @Update
    suspend fun update(classroom: Classroom)

    @Delete
    suspend fun delete(classroom: Classroom)

    suspend fun insert(building: Building)

    suspend fun update(building: Building)

    suspend fun delete(building: Building)
    suspend fun incrementRoomCount(id: Int)
    suspend fun decrementRoomCount(id: Int)
}

