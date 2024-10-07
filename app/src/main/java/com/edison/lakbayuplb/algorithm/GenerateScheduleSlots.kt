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
    val totalSlotsForDays = totalSlots * totalDays * 2  // Double the slots because we need to duplicate title and section

    // Mutable list to store the slots for all days, initialized with empty strings
    val slots = MutableList(totalSlotsForDays) { "" }

    // Populate the slots for each day
    for (dayIndex in 0 until totalDays) {
        val dayStartIndex = dayIndex * totalSlots * 2  // Double the slots per day for title and section

        when (totalSlots) {
            1 -> {
                // If the class is 1 hour (2 slots per day), place the title twice and section twice for the day
                slots[dayStartIndex] = classTitle
                slots[dayStartIndex + 1] = classTitle
                slots[dayStartIndex + 2] = section
                slots[dayStartIndex + 3] = section
            }
            else -> {
                // For durations longer than 1 hour
                val middleIndex = totalSlots / 2
                if (totalSlots % 2 == 0) {
                    // For even number of slots
                    // Leave half the slots empty and then place title twice and section twice
                    val padding = (totalSlotsForDays - (middleIndex * 2)) / 2
                    for (i in 0 until padding) {
                        slots[dayStartIndex + i] = ""
                    }
                    if(totalDays > 1) {//Classes with Two Days
                        slots[dayStartIndex + middleIndex - 1] = classTitle
                        slots[dayStartIndex + middleIndex] = classTitle
                        slots[dayStartIndex + middleIndex + 1] = section
                        slots[dayStartIndex + middleIndex + 2] = section
                    }else{//Classes with One Day Only
                        slots[middleIndex -1 ] = classTitle
                        slots[middleIndex] = section
                    }
                } else {
                    // For odd number of slots, adjust placement similarly
                    if(totalDays > 1) {//Classes with Two Days
                        if(middleIndex %2 == 1) {
                            slots[dayStartIndex + middleIndex - 1] = classTitle
                            slots[dayStartIndex + middleIndex] = classTitle
                            slots[dayStartIndex + middleIndex + 1] = section
                            slots[dayStartIndex + middleIndex + 2] = section
                        }else{
                            slots[dayStartIndex + middleIndex] = classTitle
                            slots[dayStartIndex + middleIndex + 1] = classTitle
                            if (middleIndex + 2 < totalSlots) {
                                slots[dayStartIndex + middleIndex + 2] = section
                                slots[dayStartIndex + middleIndex + 3] = section
                            }
                        }
                    }else{//Classes with One Day Only
                        slots[middleIndex-1] = classTitle
                        slots[middleIndex] = section
                    }
                }
            }
        }
    }
    return slots
}
