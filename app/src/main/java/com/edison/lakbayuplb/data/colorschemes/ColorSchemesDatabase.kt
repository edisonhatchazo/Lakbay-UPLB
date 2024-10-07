package com.edison.lakbayuplb.data.colorschemes

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ColorSchemes::class], version  = 1, exportSchema = false)
abstract class ColorSchemesDatabase : RoomDatabase(){
    abstract fun ColorSchemesDao(): ColorSchemesDao

    companion object{
        @Volatile
        private var Instance: ColorSchemesDatabase? = null

        fun getDatabase(context: Context): ColorSchemesDatabase {
            return Instance ?: synchronized(this){
                Room.databaseBuilder(
                    context,
                    ColorSchemesDatabase::class.java,
                    "colors_database"
                )
                    .createFromAsset("database/colors.db")
                    .build()
                    .also{ Instance = it}
            }
        }
    }
}

