package com.edison.lakbayuplb.algorithm

import java.time.LocalTime

fun generateScheduleSlots(
    classTitle: String,
    section: String,
    startTime: LocalTime,
    endTime: LocalTime,
    days: String
): MutableList<String> {
    val classDurationMinutes = java.time.Duration.between(startTime, endTime).toMinutes().toInt()
    val totalSlots = classDurationMinutes / 30  // Calculate how many 30-minute slots the class spans
    // Split the days string into individual days (e.g., "T, TH" -> ["T", "TH"])
    val dayList = days.split(", ")

    // The size of the slot list should multiply by the number of days
    val totalDays = dayList.size
    val totalSlotsForDays = totalSlots * totalDays  // Double the slots because we need to duplicate title and section
    val totalCount = totalDays
    val totalSpaces = (totalSlotsForDays - (totalCount * 2))/2
    // Mutable list to store the slots for all days, initialized with empty strings
    val slots = MutableList(totalSlotsForDays) { "" }
    // Populate the slots for each day
    var count = 0
    if(classDurationMinutes % 60 == 0){// Whole Hour
        for(i in 0 until totalSpaces)
            count += 1
        for(i in 0 until totalCount){
            slots[count] = classTitle
            count += 1
        }
        for(i in 0 until totalCount){
            slots[count] = section
            count += 1
        }
    }else{
        val oddSlots = (((totalSlotsForDays/totalDays)-3)/2)*totalDays
        for(i in 0 until oddSlots)
            count += 1
        for(i in 0 until totalCount){
            slots[count] = classTitle
            count += 1
        }
        for(i in 0 until totalCount){
            slots[count] = section
            count += 1
        }
    }
    return slots
}
