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
    existingSchedules: List<ClassSchedule>,
    selectedDays: List<String>,
    enabled: Boolean = true
) {
    val allHours = (7..12) + (1..6)
    val allMinutes = listOf(0, 30)
    val periods = listOf("AM", "PM")

    // Filter hours and minutes based on existing schedules
    val hours = filterHours(allHours, existingSchedules, selectedDays)
    val minutes = filterMinutes(allMinutes, existingSchedules, selectedDays)

    val initialHour = if (initialTime.hour % 12 == 0) 12 else initialTime.hour % 12
    val initialPeriod = if (initialTime.hour < 12) "AM" else "PM"
    val initialMinute = initialTime.minute

    var selectedHour by remember { mutableStateOf(initialHour) }
    var selectedMinute by remember { mutableStateOf(initialMinute) }
    var selectedPeriod by remember { mutableStateOf(initialPeriod) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        DropDownMenu(
            items = hours,
            selectedItem = initialHour,
            onItemSelected = {
                selectedHour = it
                updateTime(selectedHour, selectedMinute, selectedPeriod, onTimeChanged)
            },
            enabled = enabled
        )
        Spacer(modifier = Modifier.width(8.dp))
        DropDownMenu(
            items = minutes,
            selectedItem = initialMinute,
            onItemSelected = {
                selectedMinute = it
                updateTime(selectedHour, selectedMinute, selectedPeriod, onTimeChanged)
            },
            enabled = enabled
        )
        Spacer(modifier = Modifier.width(8.dp))
        DropDownMenu(
            items = periods,
            selectedItem = initialPeriod,
            onItemSelected = {
                selectedPeriod = it
                updateTime(selectedHour, selectedMinute, selectedPeriod, onTimeChanged)
            },
            enabled = enabled
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

// Filter functions
private fun filterHours(hours: List<Int>, schedules: List<ClassSchedule>, selectedDays: List<String>): List<Int> {
    val takenHours = schedules.flatMap { schedule ->
        val startHour = schedule.time.hour
        val endHour = schedule.timeEnd.hour
        (startHour until endHour).toList()
    }.distinct()

    return hours.filter { hour ->
        val adjustedHour = if (hour == 12) 0 else hour
        !takenHours.contains(adjustedHour)
    }
}

private fun filterMinutes(minutes: List<Int>, schedules: List<ClassSchedule>, selectedDays: List<String>): List<Int> {
    val takenMinutes = schedules.flatMap { schedule ->
        val startMinute = schedule.time.minute
        val endMinute = schedule.timeEnd.minute
        if (startMinute == 0 && endMinute == 30) listOf(0) else if (startMinute == 30 && endMinute == 0) listOf(30) else emptyList()
    }.distinct()

    return minutes.filter { minute -> !takenMinutes.contains(minute) }
}