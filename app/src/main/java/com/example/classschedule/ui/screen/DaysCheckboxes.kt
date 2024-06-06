package com.example.classschedule.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.classschedule.data.ClassSchedule

@Composable
fun DaysCheckboxes(classSchedule: ClassSchedule, modifier: Modifier = Modifier) {
    val daysOfWeek = listOf("M", "T", "W", "TH", "F", "S") // Days of the week abbreviations
    val selectedDays = classSchedule.days.split(", ").map { it.trim() } // Parse and trim the days string

    Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceBetween) {
        daysOfWeek.forEach { day ->
            val isChecked = selectedDays.contains(day)
            Checkbox(
                checked = isChecked,
                onCheckedChange = null, // Null makes it disabled (read-only)
                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
            )
            Text(text = day)
        }
    }
}