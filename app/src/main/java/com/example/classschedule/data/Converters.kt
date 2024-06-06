package com.example.classschedule.data

import androidx.room.TypeConverter
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class Converters {
    private val formatter = DateTimeFormatter.ISO_LOCAL_TIME

    @TypeConverter
    fun fromLocalTime(time: LocalTime?): String? {
        return time?.format(formatter)
    }

    @TypeConverter
    fun toLocalTime(time: String?): LocalTime? {
        return time?.let {
            LocalTime.parse(it, formatter)
        }
    }
}