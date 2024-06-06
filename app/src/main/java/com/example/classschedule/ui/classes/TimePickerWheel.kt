package com.example.classschedule.ui.classes

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
import java.time.LocalTime

@Composable
fun TimePickerWheel(
    initialTime: LocalTime,
    onTimeChanged: (LocalTime) -> Unit,
    enabled: Boolean = true
) {
    val hours = (7..12) + (1..6)
    val minutes = listOf(0, 30)
    val periods = listOf("AM", "PM")

    var selectedHour by remember { mutableStateOf(initialTime.hour % 12) }
    var selectedMinute by remember { mutableStateOf(initialTime.minute) }
    var selectedPeriod by remember { mutableStateOf(if (initialTime.hour < 12) "AM" else "PM") }

    Row(verticalAlignment = Alignment.CenterVertically) {
        DropDownMenu(
            items = hours,
            selectedItem = selectedHour,
            onItemSelected = {
                selectedHour = it
                updateTime(selectedHour, selectedMinute, selectedPeriod, onTimeChanged)
            },
            enabled = enabled
        )
        Spacer(modifier = Modifier.width(8.dp))
        DropDownMenu(
            items = minutes,
            selectedItem = selectedMinute,
            onItemSelected = {
                selectedMinute = it
                updateTime(selectedHour, selectedMinute, selectedPeriod, onTimeChanged)
            },
            enabled = enabled
        )
        Spacer(modifier = Modifier.width(8.dp))
        DropDownMenu(
            items = periods,
            selectedItem = selectedPeriod,
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
                    text = {Text(text = item.toString())},
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
