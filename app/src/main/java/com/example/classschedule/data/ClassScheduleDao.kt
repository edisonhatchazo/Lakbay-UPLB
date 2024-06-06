package com.example.classschedule.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ClassScheduleDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(classSchedule: ClassSchedule)

    @Update
    suspend fun update(classSchedule: ClassSchedule)

    @Delete
    suspend fun delete(classSchedule: ClassSchedule)

    @Query("SELECT * from classes WHERE id = :id")
    fun getClassSchedule(id: Int): Flow<ClassSchedule>

    @Query("SELECT * from classes WHERE days LIKE '%' || :day || '%' ORDER BY title ASC")
    fun getClassScheduleByDay(day: String): Flow<List<ClassSchedule>>

    @Query("SELECT * from classes ORDER BY title ASC")
    fun getAllClassSchedule(): Flow<List<ClassSchedule>>
}