package com.edison.lakbayuplb.data.building

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.edison.lakbayuplb.data.building.Building
import com.edison.lakbayuplb.data.building.Classroom
import com.edison.lakbayuplb.data.building.Pins
import kotlinx.coroutines.flow.Flow

@Dao
interface BuildingDao {

    @Query("SELECT * FROM Buildings ORDER BY college ASC")
    fun getAllBuildings(): Flow<List<Building>>

    @Query("""
        SELECT * FROM Buildings 
        WHERE name LIKE '%' || :query || '%' 
        OR abbreviation LIKE '%' || :query || '%'
        OR college LIKE '%' || :query || '%'
        ORDER BY college ASC
    """)
    fun searchBuildings(query: String): Flow<List<Building>>

    @Query("SELECT * FROM Buildings WHERE building_id = :id")
    fun getBuilding(id: Int): Flow<Building>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(building: Building)

    @Update
    suspend fun update(building: Building)

    @Delete
    suspend fun delete(building: Building)
    @Query("UPDATE Buildings SET room_count = room_count + 1 WHERE building_id = :id")
    suspend fun incrementRoomCount(id: Int)
    @Query("UPDATE Buildings SET room_count = room_count - 1 WHERE building_id = :id")
    suspend fun decrementRoomCount(id: Int)

}

@Dao
interface ClassroomDao {
    @Query("""
        SELECT * FROM Rooms 
        WHERE (title LIKE '%' || :query || '%'
        OR abbreviation LIKE '%' || :query || '%')
        AND type LIKE 'Room'
        ORDER BY title ASC
    """)
    fun searchRooms(query: String): Flow<List<Classroom>>

    @Query("SELECT * FROM Rooms WHERE room_Id = :id")
    fun getRoom(id: Int): Flow<Classroom>

    @Query("SELECT * FROM Rooms WHERE building_Id = :buildingId")
    fun getRoomsByBuildingId(buildingId: Int): Flow<List<Classroom>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(classroom: Classroom)

    @Update
    suspend fun update(classroom: Classroom)

    @Delete
    suspend fun delete(classroom: Classroom)
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