package com.edison.lakbayuplb.data

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
    suspend fun insert(classSchedule: com.edison.lakbayuplb.data.ClassSchedule)

    @Update
    suspend fun update(classSchedule: com.edison.lakbayuplb.data.ClassSchedule)

    @Delete
    suspend fun delete(classSchedule: com.edison.lakbayuplb.data.ClassSchedule)

    @Query("SELECT * from classes WHERE id = :id")
    fun getClassSchedule(id: Int): Flow<com.edison.lakbayuplb.data.ClassSchedule>

    @Query("SELECT * from classes WHERE days LIKE '%' || :day || '%' ORDER BY title ASC")
    fun getClassScheduleByDay(day: String): Flow<List<com.edison.lakbayuplb.data.ClassSchedule>>

    @Query("SELECT * FROM classes where roomId = :roomId")
    fun getLocation(roomId: Int): Flow<com.edison.lakbayuplb.data.ClassSchedule>


    @Query("SELECT * from classes ORDER BY title ASC")
    fun getAllClassSchedule(): Flow<List<com.edison.lakbayuplb.data.ClassSchedule>>
}

@Dao
interface ExamScheduleDao{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(examSchedule: com.edison.lakbayuplb.data.ExamSchedule)

    @Update
    suspend fun update(examSchedule: com.edison.lakbayuplb.data.ExamSchedule)

    @Delete
    suspend fun delete(examSchedule: com.edison.lakbayuplb.data.ExamSchedule)

    @Query("SELECT * from exams WHERE id = :id")
    fun getExamSchedule(id: Int): Flow<com.edison.lakbayuplb.data.ExamSchedule>

    @Query("SELECT * FROM exams where roomId = :roomId")
    fun getLocation(roomId: Int): Flow<com.edison.lakbayuplb.data.ExamSchedule>

    @Query("SELECT * from exams ORDER BY title ASC")
    fun getAllExamSchedule(): Flow<List<com.edison.lakbayuplb.data.ExamSchedule>>
}

