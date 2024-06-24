package com.example.classschedule.ui.buildingScreens.uplb

import com.example.classschedule.data.Building
import com.example.classschedule.data.Classroom

data class BuildingUiState(
    val buildingDetails: BuildingDetails = BuildingDetails(),
    val isEntryValid: Boolean = false
)

data class ClassroomUiState(
    val classroomDetails: ClassroomDetails = ClassroomDetails(),
    val isEntryValid: Boolean = false
)

data class BuildingDetails(
    val buildingId: Int = 0,
    val title: String = "",
    val abbreviation: String = "",
    val name: String = "",
    val latitude: Double = 14.16747822735461,
    val longitude: Double = 121.24338486047947
)


fun BuildingDetails.toBuilding(): Building = Building(
    buildingId = buildingId,
    title = title,
    abbreviation = abbreviation,
    name = name,
    latitude = latitude,
    longitude = longitude
)

fun Building.toBuildingDetails(): BuildingDetails = BuildingDetails(
    buildingId = buildingId,
    title = title,
    abbreviation = abbreviation,
    name = name,
    latitude = latitude,
    longitude = longitude
)

fun Building.toBuildingUiState(isEntryValid: Boolean = false): BuildingUiState = BuildingUiState(
    buildingDetails = this.toBuildingDetails(),
    isEntryValid = isEntryValid
)

data class ClassroomDetails(
    val roomId: Int = 0,
    val title: String = "",
    val abbreviation: String = "",
    val floor: String = "",
    val latitude: Double = 14.16747822735461,
    val longitude: Double = 121.24338486047947,
    val buildingId: Int = 0
)

fun ClassroomDetails.toClassroom(): Classroom = Classroom(
    roomId = roomId,
    title = title,
    abbreviation = abbreviation,
    floor = floor,
    latitude = latitude,
    longitude = longitude,
    buildingId = buildingId
)

fun Classroom.toClassroomDetails(): ClassroomDetails = ClassroomDetails(
    roomId = roomId,
    title = title,
    abbreviation = abbreviation,
    floor = floor,
    latitude = latitude,
    longitude = longitude,
    buildingId = buildingId
)


fun Classroom.toClassroomUiState(isEntryValid: Boolean = false): ClassroomUiState = ClassroomUiState(
    classroomDetails = this.toClassroomDetails(),
    isEntryValid = isEntryValid
)