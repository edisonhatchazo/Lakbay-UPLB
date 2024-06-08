package com.example.classschedule.algorithm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun DaysSelectionCheckboxes(
    selectedDays: List<String>,
    onDaySelected: (String, Boolean) -> Unit, // Callback to handle changes
    modifier: Modifier = Modifier
) {
    val daysOfWeek = listOf("M", "T", "W", "TH", "F", "S") // Days of the week abbreviations
    Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceBetween) {
        daysOfWeek.forEach { day ->
            val isChecked = selectedDays.contains(day)
            // Disable checkbox if already two are selected and this one isn't checked
            val isEnabled = selectedDays.size < 2 || isChecked
            Text(text = day)
            Checkbox(
                checked = isChecked,
                onCheckedChange = { shouldCheck ->
                    onDaySelected(day, shouldCheck)
                },
                enabled = isEnabled,
                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
            )

        }
    }
}