package com.edison.lakbayuplb.data.classes

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalTime

@Entity(tableName = "classes")
data class ClassSchedule (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val section: String,
    val teacher: String,
    val roomId: Int,
    val location: String,
    val time: LocalTime,
    val timeEnd: LocalTime,
    val days: String,  // Store days as a single string
    val colorId: Int
){
    override fun toString(): String {
        val formattedTime = "${time.hour}:${time.minute} ${if (time.hour < 12) "AM" else "PM"}"
        val formattedTimeEnd = "${timeEnd.hour}:${timeEnd.minute} ${if (timeEnd.hour < 12) "AM" else "PM"}"
        return "ClassSchedule(id=$id, title='$title', location='$location', time=$formattedTime, timeEnd=$formattedTimeEnd, days='$days')"
    }
}

@Entity(tableName = "exams")
data class ExamSchedule (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val section: String,
    val teacher: String,
    val roomId: Int,
    val location: String,
    val date: String,  // Store date as a string or LocalDate
    val time: LocalTime,
    val timeEnd: LocalTime,
    val colorId: Int,
    val day: String
)

