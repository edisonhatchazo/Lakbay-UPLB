package com.example.classschedule.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Building::class, Classroom::class], version = 1, exportSchema = false)
abstract class BuildingDatabase : RoomDatabase() {
    abstract fun buildingDao(): BuildingDao
    abstract fun classroomDao(): ClassroomDao

    companion object {
        @Volatile
        private var INSTANCE: BuildingDatabase? = null

        fun getDatabase(context: Context): BuildingDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    BuildingDatabase::class.java,
                    "building_database"
                )
                    .createFromAsset("database/buildingDB.db")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}