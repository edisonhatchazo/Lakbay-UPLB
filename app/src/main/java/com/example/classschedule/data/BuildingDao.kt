package com.example.classschedule.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BuildingDao {

    @Query("SELECT * FROM Buildings ORDER BY title ASC")
    fun getAllBuildings(): Flow<List<Building>>

    @Query("""
        SELECT * FROM Buildings 
        WHERE title LIKE '%' || :query || '%' 
        OR abbreviation LIKE '%' || :query || '%'
        OR name LIKE '%' || :query || '%'
        ORDER BY title ASC
    """)
    fun searchBuildings(query: String): Flow<List<Building>>

    @Query("SELECT * FROM Buildings WHERE building_id = :id")
    fun getBuilding(id: Int): Flow<Building>

}

@Dao
interface ClassroomDao {
    @Query("""
        SELECT * FROM Rooms 
        WHERE title LIKE '%' || :query || '%'
        OR abbreviation LIKE '%' || :query || '%'
        ORDER BY title ASC
    """)
    fun searchRooms(query: String): Flow<List<Classroom>>

    @Query("SELECT * FROM Rooms WHERE room_Id = :id")
    fun getRoom(id: Int): Flow<Classroom>

    @Query("SELECT * FROM Rooms WHERE building_Id = :buildingId")
    fun getRoomsByBuildingId(buildingId: Int): Flow<List<Classroom>>
}

@Dao
interface PinsDao{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(pins: Pins)

    @Update
    suspend fun update(pins: Pins)

    @Delete
    suspend fun delete(pins: Pins)

    @Query("SELECT * from pins where id = :id")
    fun getPin(id: Int): Flow<Pins>

    @Query("SELECT * from pins ORDER BY title ASC")
    fun getAllPins(): Flow<List<Pins>>
}