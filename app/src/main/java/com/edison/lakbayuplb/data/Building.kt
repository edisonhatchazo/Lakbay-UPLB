package com.edison.lakbayuplb.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pins")
data class Pins(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val floor: String,
    val latitude: Double,
    val longitude: Double,
    val colorId: Int
)

@Entity(tableName = "Buildings")
data class Building(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "building_id") val buildingId: Int,
    val name: String,
    @ColumnInfo(name = "other_name") val otherName: String?,
    val abbreviation: String,
    val college: String,
    val latitude: Double,
    val longitude: Double,
    @ColumnInfo(name = "room_count")  val roomCount: Int,
)

@Entity(tableName = "Rooms")
data class Classroom(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "room_id")val roomId: Int,
    val title: String,
    val abbreviation: String,
    val floor: String,
    val type: String,
    val latitude: Double,
    val longitude: Double,
    val college: String,
    @ColumnInfo(name = "building_id")
    val buildingId: Int
)

