package com.edison.lakbayuplb.ui.buildingScreens.uplb

import com.edison.lakbayuplb.data.Classroom
import com.edison.lakbayuplb.data.Building
import com.edison.lakbayuplb.ui.buildingScreens.uplb.buildings.BuildingUiState
import com.edison.lakbayuplb.ui.buildingScreens.uplb.rooms.ClassroomUiState


data class BuildingDetails(
    val buildingId: Int = 0,
    val name: String = "",
    val otherName: String? = "",
    val abbreviation: String = "",
    val college: String = "",
    val latitude: Double = 14.16747822735461,
    val longitude: Double = 121.24338486047947,
    val roomCount: Int = 0
)


fun Building.toBuildingDetails(): BuildingDetails = BuildingDetails(
    buildingId = buildingId,
    college = college,
    abbreviation = abbreviation,
    name = name,
    otherName = otherName,
    latitude = latitude,
    longitude = longitude,
    roomCount = roomCount
)

fun Building.toBuildingUiState(isEntryValid: Boolean = false): BuildingUiState = BuildingUiState(
    buildingDetails = this.toBuildingDetails(),
    isEntryValid = isEntryValid
)



fun BuildingDetails.toBuilding(): Building = Building(
    buildingId = buildingId,
    college = college,
    abbreviation = abbreviation,
    name = name,
    otherName = otherName,
    latitude = latitude,
    longitude = longitude,
    roomCount = roomCount
)




data class ClassroomDetails(
    val roomId: Int = 0,
    val title: String = "",
    val abbreviation: String = "",
    val type:String = "",
    val floor: String = "",
    val college: String = "",
    val latitude: Double = 14.16747822735461,
    val longitude: Double = 121.24338486047947,
    val buildingId: Int = 0,
)


fun Classroom.toClassroomDetails(): ClassroomDetails = ClassroomDetails(
    roomId = roomId,
    title = title,
    abbreviation = abbreviation,
    floor = floor,
    type = type,
    latitude = latitude,
    longitude = longitude,
    buildingId = buildingId,
    college = college,
)

fun Classroom.toClassroomUiState(isEntryValid: Boolean = false): ClassroomUiState = ClassroomUiState(
    classroomDetails = this.toClassroomDetails(),
    isEntryValid = isEntryValid
)


fun ClassroomDetails.toClassroom(): Classroom = Classroom(
    roomId = roomId,
    title = title,
    abbreviation = abbreviation,
    floor = floor,
    type = type,
    latitude = latitude,
    longitude = longitude,
    buildingId = buildingId,
    college = college,
)