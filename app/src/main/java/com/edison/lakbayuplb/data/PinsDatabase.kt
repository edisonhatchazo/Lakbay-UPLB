package com.edison.lakbayuplb.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Pins::class], version = 1, exportSchema = false)
abstract class PinsDatabase : RoomDatabase(){
    abstract fun PinsDao(): PinsDao

    companion object{
        @Volatile
        private var Instance: PinsDatabase? = null

        fun getDatabase(context: Context): PinsDatabase{
            return Instance ?: synchronized(this){
                Room.databaseBuilder(context, PinsDatabase::class.java,"pins_database")
                    .build()
                    .also{Instance = it}
            }
        }
    }
}