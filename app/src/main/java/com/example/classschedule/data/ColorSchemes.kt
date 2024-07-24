package com.example.classschedule.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "colors")
data class ColorSchemes(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val backgroundColor: Int,
    val fontColor: Int,
    val isCurrentlyUsed: Int = 0
)

@Dao
interface ColorSchemesDao {

    @Query("SELECT * FROM colors")
    fun getAllColorSchemes(): Flow<List<ColorSchemes>>

    @Query("SELECT * FROM colors WHERE id = :id")
    fun getColorSchemeById(id: Int): Flow<ColorSchemes?>

    @Query("SELECT * FROM colors WHERE name = :name")
    fun getColorSchemeByName(name: String): ColorSchemes?


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertColorScheme(colorScheme: ColorSchemes)

    @Update
    suspend fun updateColorScheme(colorScheme: ColorSchemes)

    @Delete
    suspend fun deleteColorScheme(colorScheme: ColorSchemes)

    @Query("UPDATE colors SET isCurrentlyUsed = isCurrentlyUsed + 1 WHERE id = :id")
    suspend fun incrementIsCurrentlyUsed(id: Int)
    @Query("UPDATE colors SET isCurrentlyUsed = isCurrentlyUsed - 1 WHERE id = :id")
    suspend fun decrementIsCurrentlyUsed(id: Int)
}