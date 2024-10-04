package com.edison.lakbayuplb.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.edison.lakbayuplb.algorithm.LocalTimeConverter

@Database(entities = [com.edison.lakbayuplb.data.ClassSchedule::class], version = 1, exportSchema = false)
@TypeConverters(LocalTimeConverter::class)
abstract class ClassScheduleDatabase : RoomDatabase(){
    abstract fun ClassScheduleDao(): ClassScheduleDao

    companion object{
        @Volatile
        private var Instance: ClassScheduleDatabase? = null

        fun getDatabase(context: Context): ClassScheduleDatabase{
            return Instance ?: synchronized(this){
                Room.databaseBuilder(context, ClassScheduleDatabase::class.java,"class_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also{Instance = it}
            }
        }
    }
}