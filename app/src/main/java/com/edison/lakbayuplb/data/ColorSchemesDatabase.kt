package com.edison.lakbayuplb.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [com.edison.lakbayuplb.data.ColorSchemes::class], version  = 1, exportSchema = false)
abstract class ColorSchemesDatabase : RoomDatabase(){
    abstract fun ColorSchemesDao(): com.edison.lakbayuplb.data.ColorSchemesDao

    companion object{
        @Volatile
        private var Instance: com.edison.lakbayuplb.data.ColorSchemesDatabase? = null

        fun getDatabase(context: Context): com.edison.lakbayuplb.data.ColorSchemesDatabase {
            return com.edison.lakbayuplb.data.ColorSchemesDatabase.Companion.Instance ?: synchronized(this){
                Room.databaseBuilder(
                    context,
                    com.edison.lakbayuplb.data.ColorSchemesDatabase::class.java,
                    "colors_database"
                )
                    .createFromAsset("database/colors.db")
                    .build()
                    .also{ com.edison.lakbayuplb.data.ColorSchemesDatabase.Companion.Instance = it}
            }
        }
    }
}

