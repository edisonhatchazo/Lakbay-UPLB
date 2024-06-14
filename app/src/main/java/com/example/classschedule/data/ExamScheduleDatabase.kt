package com.example.classschedule.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.classschedule.algorithm.LocalTimeConverter

@Database(entities = [ExamSchedule::class], version = 1, exportSchema = false)
@TypeConverters(LocalTimeConverter::class)
abstract class ExamScheduleDatabase : RoomDatabase() {
    abstract fun ExamScheduleDao(): ExamScheduleDao

    companion object{
        @Volatile
        private var Instance: ExamScheduleDatabase? = null

        fun getDatabase(context: Context): ExamScheduleDatabase{
            return Instance ?: synchronized(this){
                Room.databaseBuilder(context, ExamScheduleDatabase::class.java,"exam_database")
                    .build()
                    .also{Instance = it}
            }
        }
    }
}
