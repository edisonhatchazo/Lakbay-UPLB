package com.edison.lakbayuplb.algorithm

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.edison.lakbayuplb.data.classes.ClassSchedule
import com.edison.lakbayuplb.data.classes.ExamSchedule
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// Define the AM/PM formatter for display
val amPmFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("h:mm a")

@Composable
fun TimePickerWheel(
    initialTime: LocalTime,
    onTimeChanged: (LocalTime) -> Unit,
    availableTimes: List<LocalTime>,  // List of available LocalTime
    enabled: Boolean = true
) {
    var selectedTime by remember { mutableStateOf(initialTime) }

    DropDownMenu(
        items = availableTimes,
        selectedItem = selectedTime,
        onItemSelected = { time ->
            selectedTime = time
            onTimeChanged(time)
        },
        enabled = enabled
    )
}

@Composable
fun DropDownMenu(
    items: List<LocalTime>,  // Specifically LocalTime for your case
    selectedItem: LocalTime,
    onItemSelected: (LocalTime) -> Unit,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        TextButton(onClick = { expanded = true }, enabled = enabled) {
            // Format the selected item for display (AM/PM format)
            Text(text = selectedItem.format(amPmFormatter))
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        // Display the LocalTime formatted in AM/PM
                        Text(text = item.format(amPmFormatter))
                    },
                    onClick = {
                        onItemSelected(item)  // Return the original LocalTime object when selected
                        expanded = false
                    },
                    enabled = enabled
                )
            }
        }
    }
}

fun calculateAvailableStartTimes(
    existingSchedules: List<ClassSchedule>,
    selectedDays: List<String>,
    editingClassSchedule: ClassSchedule? = null // Optional parameter for the class being edited
): List<LocalTime> {
    // Create a list of all possible start times
    val allStartTimes = (7..18).flatMap { hour ->
        listOf(LocalTime.of(hour, 0), LocalTime.of(hour, 30))
    }.toMutableList()

    allStartTimes.remove(LocalTime.of(18, 30))

    // Filter to find occupied times, excluding the currently edited class schedule if provided
    val occupiedTimes = existingSchedules.filter { schedule ->
        schedule.id != editingClassSchedule?.id && // Exclude the schedule being edited
                selectedDays.any { it in schedule.days.split(", ") }
    }.flatMap { schedule ->
        generateSequence(schedule.time) { it.plusMinutes(30) }
            .takeWhile { it.isBefore(schedule.timeEnd) }
            .toList()
    }

    // Filter out the occupied times and the 30-minute slots before any occupied times
    val availableStartTimes = allStartTimes.filterNot { time ->
        occupiedTimes.any { occupiedTime ->
            time == occupiedTime || time.plusMinutes(30) == occupiedTime
        }
    }.filterNot { it == LocalTime.of(18, 30) } // Always remove 6:30 PM if it is available

    return availableStartTimes
}

fun calculateAvailableEndTimes(
    existingSchedules: List<ClassSchedule>,
    selectedDays: List<String>,
    startTime: LocalTime,
    editingClassSchedule: ClassSchedule? = null
): List<LocalTime> {
    val filteredSchedules = existingSchedules.filter { it.id != editingClassSchedule?.id }

    // Generate all possible end times starting from 1 hour after the start time
    val allEndTimes = generateSequence(startTime.plusMinutes(60)) { it.plusMinutes(30) }
        .takeWhile { it.isBefore(LocalTime.of(19, 30)) } // Includes up to 7:00 PM
        .toList()

    // Find the nearest occupied time slot after the start time, excluding the editing class schedule
    val occupiedTimes = filteredSchedules.filter { schedule ->
        selectedDays.any { it in schedule.days.split(", ") }
    }.flatMap { schedule ->
        generateSequence(schedule.time) { it.plusMinutes(30) }
            .takeWhile { it.isBefore(schedule.timeEnd) }
            .toList()
    }

    // Determine the limit time for the end time options
    val nearestOccupiedTime = occupiedTimes.filter { it.isAfter(startTime) }
        .minOrNull()

    val limitTime = nearestOccupiedTime ?: LocalTime.of(19, 0)

    // Filter the end times to ensure they do not go past the nearest occupied time slot
    val availableEndTimes = allEndTimes.filter { it.isBefore(limitTime) || it == limitTime }

    // Ensure the end times are at least 1 hour after the start time and sorted
    return availableEndTimes.filter { it.isAfter(startTime.plusMinutes(60)) || it == startTime.plusMinutes(60) }
        .sorted()
}


fun calculateExamAvailableStartTimes(
    existingSchedules: List<ExamSchedule>,
    selectedDay: String, // Now using a single selected day instead of a list
    selectedDate: String, // Add selected date to the parameters
    editingExamSchedule: ExamSchedule? = null // Optional parameter for the class being edited
): List<LocalTime> {
    // Create a list of all possible start times
    val allStartTimes = (7..20).flatMap { hour ->
        listOf(LocalTime.of(hour, 0), LocalTime.of(hour, 30))
    }.toMutableList()

    allStartTimes.remove(LocalTime.of(20, 30))

    // Filter to find occupied times, excluding the currently edited exam schedule if provided
    val occupiedTimes = existingSchedules.filter { schedule ->
        schedule.id != editingExamSchedule?.id &&
                schedule.day == selectedDay &&
                schedule.date == selectedDate // Exclude the schedule being edited and filter by day and date
    }.flatMap { schedule ->
        generateSequence(schedule.time) { it.plusMinutes(30) }
            .takeWhile { it.isBefore(schedule.timeEnd) }
            .toList()
    }

    // Filter out the occupied times and the 30-minute slots before any occupied times
    val availableStartTimes = allStartTimes.filterNot { time ->
        occupiedTimes.any { occupiedTime ->
            time == occupiedTime || time.plusMinutes(30) == occupiedTime
        }
    }.filterNot { it == LocalTime.of(20, 30) }

    return availableStartTimes
}
fun calculateExamAvailableEndTimes(
    existingSchedules: List<ExamSchedule>,
    selectedDay: String, // Now using a single selected day instead of a list
    selectedDate: String, // Add selected date to the parameters
    startTime: LocalTime,
    editingExamSchedule: ExamSchedule? = null
): List<LocalTime> {
    val filteredSchedules = existingSchedules.filter { it.id != editingExamSchedule?.id }

    // Generate all possible end times starting from 1 hour after the start time
    val allEndTimes = generateSequence(startTime.plusMinutes(60)) { it.plusMinutes(30) }
        .takeWhile { it.isBefore(LocalTime.of(21, 30)) } // Includes up to 9:00 PM
        .toList()

    // Find the nearest occupied time slot after the start time, excluding the editing exam schedule
    val occupiedTimes = filteredSchedules.filter { schedule ->
        schedule.day == selectedDay && schedule.date == selectedDate // Filter by day and date
    }.flatMap { schedule ->
        generateSequence(schedule.time) { it.plusMinutes(30) }
            .takeWhile { it.isBefore(schedule.timeEnd) }
            .toList()
    }

    // Determine the limit time for the end time options
    val nearestOccupiedTime = occupiedTimes.filter { it.isAfter(startTime) }
        .minOrNull()

    val limitTime = nearestOccupiedTime ?: LocalTime.of(21, 0)

    // Filter the end times to ensure they do not go past the nearest occupied time slot
    val availableEndTimes = allEndTimes.filter { it.isBefore(limitTime) || it == limitTime }

    // Ensure the end times are at least 1 hour after the start time and sorted
    return availableEndTimes.filter { it.isAfter(startTime.plusMinutes(60)) || it == startTime.plusMinutes(60) }
        .sorted()
}