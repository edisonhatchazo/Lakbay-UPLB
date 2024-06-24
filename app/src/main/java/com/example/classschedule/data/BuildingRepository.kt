package com.example.classschedule.data

import kotlinx.coroutines.flow.Flow

interface BuildingRepository {
    fun getAllBuildings(): Flow<List<Building>>

    fun searchBuildings(query: String): Flow<List<Building>>

    fun getBuilding(id: Int): Flow<Building>

    fun searchRooms(query: String): Flow<List<Classroom>>

    fun getRoom(id: Int): Flow<Classroom>

    fun getRoomsByBuildingId(buildingId: Int): Flow<List<Classroom>>
}

