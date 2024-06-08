package com.example.classschedule.algorithm

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.classschedule.data.ClassSchedule
import java.time.LocalTime

@Composable
fun TimePickerWheel(
    initialTime: LocalTime,
    onTimeChanged: (LocalTime) -> Unit,
    availableTimes: List<LocalTime>,  // List of available LocalTime
    enabled: Boolean = true
) {
    val hours = availableTimes.map { it.hour }.distinct().sorted()
    val minutes = availableTimes.filter { it.hour == initialTime.hour }.map { it.minute }.distinct().sorted()

    var selectedHour by remember { mutableStateOf(initialTime.hour) }
    var selectedMinute by remember { mutableStateOf(initialTime.minute) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        DropDownMenu(
            items = hours,
            selectedItem = selectedHour,
            onItemSelected = { hour ->
                selectedHour = hour
                // Adjust minutes based on selected hour
                selectedMinute = availableTimes.firstOrNull { it.hour == hour }?.minute ?: minutes.firstOrNull() ?: 0
                onTimeChanged(LocalTime.of(selectedHour, selectedMinute))
            },
            enabled = enabled
        )
        Spacer(modifier = Modifier.width(8.dp))
        DropDownMenu(
            items = minutes,
            selectedItem = selectedMinute,
            onItemSelected = { minute ->
                selectedMinute = minute
                onTimeChanged(LocalTime.of(selectedHour, selectedMinute))
            },
            enabled = enabled and minutes.isNotEmpty()
        )
    }
}

@Composable
fun <T> DropDownMenu(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        TextButton(onClick = { expanded = true }, enabled = enabled) {
            Text(text = selectedItem.toString())
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(text = item.toString()) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    },
                    enabled = enabled
                )
            }
        }
    }
}
private fun updateTime(hour: Int, minute: Int, period: String, onTimeChange: (LocalTime) -> Unit) {
    val adjustedHour = if (period == "PM" && hour != 12) hour + 12 else if (period == "AM" && hour == 12) 0 else hour
    val newTime = LocalTime.of(adjustedHour, minute)
    onTimeChange(newTime)
}

fun filterStartHours(hours: List<Int>, schedules: List<ClassSchedule>, selectedDays: List<String>, currentSchedule: ClassSchedule?): List<Int> {
    val takenHours = schedules.flatMap { schedule ->
        if (schedule != currentSchedule && schedule.days.split(", ").any { it in selectedDays }) {
            val startHour = schedule.time.hour
            val endHour = schedule.timeEnd.hour
            (startHour until endHour).toList()
        } else {
            emptyList()
        }
    }.distinct()

    return hours.filter { hour ->
        val adjustedHour = if (hour == 12) 0 else hour
        !takenHours.contains(adjustedHour)
    }
}

fun filterStartMinutes(minutes: List<Int>, schedules: List<ClassSchedule>, selectedDays: List<String>, currentSchedule: ClassSchedule?, startHour: Int): List<Int> {
    val takenMinutes = schedules.flatMap { schedule ->
        if (schedule != currentSchedule && schedule.days.split(", ").any { it in selectedDays }) {
            if (schedule.time.hour == startHour) {
                listOf(schedule.time.minute)
            } else {
                emptyList()
            }
        } else {
            emptyList()
        }
    }.distinct()

    return minutes.filter { minute -> !takenMinutes.contains(minute) }
}

fun filterEndHours(hours: List<Int>, schedules: List<ClassSchedule>, selectedDays: List<String>, startTime: LocalTime, currentSchedule: ClassSchedule?): List<Int> {
    val takenHours = schedules.flatMap { schedule ->
        if (schedule != currentSchedule && schedule.days.split(", ").any { it in selectedDays }) {
            val startHour = schedule.time.hour
            val endHour = schedule.timeEnd.hour
            (startHour until endHour).toList()
        } else {
            emptyList()
        }
    }.distinct()

    return hours.filter { hour ->
        val adjustedHour = if (hour == 12) 0 else hour
        !takenHours.contains(adjustedHour) || adjustedHour > startTime.hour
    }
}

fun filterEndMinutes(minutes: List<Int>, schedules: List<ClassSchedule>, selectedDays: List<String>, startTime: LocalTime, currentSchedule: ClassSchedule?): List<Int> {
    val takenMinutes = schedules.flatMap { schedule ->
        if (schedule != currentSchedule && schedule.days.split(", ").any { it in selectedDays }) {
            val startMinute = schedule.time.minute
            val endMinute = schedule.timeEnd.minute
            if (schedule.time.hour == startTime.hour && startMinute == 0 && endMinute == 30) listOf(0) else if (startMinute == 30 && endMinute == 0) listOf(30) else emptyList()
        } else {
            emptyList()
        }
    }.distinct()

    return minutes.filter { minute -> !takenMinutes.contains(minute) || (minute > startTime.minute && startTime.hour == startTime.hour) }
}

fun calculateAvailableStartTimes(existingSchedules: List<ClassSchedule>, selectedDays: List<String>): List<LocalTime> {
    val allStartTimes = (7..18).flatMap { hour ->
        listOf(LocalTime.of(hour, 0), LocalTime.of(hour, 30))
    }

    val occupiedTimes = existingSchedules.filter { schedule ->
        selectedDays.any { it in schedule.days.split(", ") }
    }.flatMap { schedule ->
        generateSequence(schedule.time) { it.plusMinutes(30) }
            .takeWhile { it.isBefore(schedule.timeEnd) }
            .toList()
    }

    return allStartTimes.filterNot { it in occupiedTimes }
}

fun calculateAvailableEndTimes(existingSchedules: List<ClassSchedule>, selectedDays: List<String>, startTime: LocalTime): List<LocalTime> {
    val allEndTimes = generateSequence(startTime.plusMinutes(30)) { it.plusMinutes(30) }
        .takeWhile { it.isBefore(LocalTime.of(19, 0)) }
        .toList()

    val occupiedTimes = existingSchedules.filter { schedule ->
        selectedDays.any { it in schedule.days.split(", ") }
    }.flatMap { schedule ->
        generateSequence(schedule.time) { it.plusMinutes(30) }
            .takeWhile { it.isBefore(schedule.timeEnd) }
            .toList()
    }

    return allEndTimes.filterNot { it in occupiedTimes }
}