package com.example.classschedule.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

@Entity(tableName = "MapData")
data class MapData(
    @PrimaryKey
    val mapId: Int = 0,
    val title: String,
    val latitude: Double,
    val longitude: Double,
    val snippet: String
)


@Dao
interface MapDataDao {

    @Query("SELECT * FROM MapData WHERE mapId = :id")
    fun getMapData(id: Int): Flow<MapData>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMapData(mapData: MapData)

    @Update
    suspend fun updateMapData(mapData: MapData)
}

interface MapDataRepository {
    fun getMapData(id: Int): Flow<MapData?>

    suspend fun insertMapData(mapData: MapData)

    suspend fun updateMapData(mapData: MapData)

    suspend fun insertOrUpdateMapData(mapData: MapData)

}

class OfflineMapDataRepository(private val mapDataDao: MapDataDao): MapDataRepository {
    override fun getMapData(id: Int): Flow<MapData?> = mapDataDao.getMapData(id)

    override suspend fun insertMapData(mapData: MapData) = mapDataDao.insertMapData(mapData)

    override suspend fun updateMapData(mapData: MapData) = mapDataDao.updateMapData(mapData)

    override suspend fun insertOrUpdateMapData(mapData: MapData){
        if (mapDataDao.getMapData(0).firstOrNull() != null) {
            mapDataDao.updateMapData(mapData)
        } else {
            mapDataDao.insertMapData(mapData)
        }
    }
}

@Database(entities = [MapData::class], version = 1, exportSchema = false)
abstract class MapDatabase : RoomDatabase(){
    abstract fun MapDataDao(): MapDataDao

    companion object{
        @Volatile
        private var Instance: MapDatabase? = null

        fun getDatabase(context: Context): MapDatabase{
            return Instance ?: synchronized(this){
                Room.databaseBuilder(context, MapDatabase::class.java,"map_data_database")
                    .build()
                    .also{Instance = it}
            }
        }
    }
}