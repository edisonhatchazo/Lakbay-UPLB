package com.example.classschedule.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.classschedule.algorithm.LocalTimeConverter

@Database(entities = [ClassSchedule::class], version = 1, exportSchema = false)
@TypeConverters(LocalTimeConverter::class)
abstract class ClassScheduleDatabase : RoomDatabase(){
    abstract fun ClassScheduleDao(): ClassScheduleDao

    companion object{
        @Volatile
        private var Instance: ClassScheduleDatabase? = null

        fun getDatabase(context: Context): ClassScheduleDatabase{
            return Instance ?: synchronized(this){
                Room.databaseBuilder(context, ClassScheduleDatabase::class.java,"class_database")
                    .build()
                    .also{Instance = it}
            }
        }
    }
}