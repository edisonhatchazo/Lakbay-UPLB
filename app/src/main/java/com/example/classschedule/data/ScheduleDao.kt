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

@Dao
interface ExamScheduleDao{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(examSchedule: ExamSchedule)

    @Update
    suspend fun update(examSchedule: ExamSchedule)

    @Delete
    suspend fun delete(examSchedule: ExamSchedule)

    @Query("SELECT * from exams WHERE id = :id")
    fun getExamSchedule(id: Int): Flow<ExamSchedule>


    @Query("SELECT * from exams ORDER BY title ASC")
    fun getAllExamSchedule(): Flow<List<ExamSchedule>>
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