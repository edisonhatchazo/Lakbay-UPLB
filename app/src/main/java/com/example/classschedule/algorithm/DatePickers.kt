package com.example.classschedule.algorithm

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickers(
    initialDate: String?,
    onDateSelected: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.US)
    val initialDateMillis = if (initialDate.isNullOrEmpty()) {
        MaterialDatePicker.todayInUtcMilliseconds()
    } else {
        try {
            sdf.parse(initialDate)?.time ?: MaterialDatePicker.todayInUtcMilliseconds()
        } catch (e: ParseException) {
            MaterialDatePicker.todayInUtcMilliseconds()
        }
    }

    val datePickerState = rememberDatePickerState(initialDateMillis, initialDisplayMode = DisplayMode.Picker)
    val calendarPickerMainColor = Color(0xFF722276)

    DatePickerDialog(
        colors = DatePickerDefaults.colors(
            containerColor = Color(0xFFF5F0FF),
        ),
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = millis.convertMillisToDate()
                        val dayOfWeek = SimpleDateFormat("E", Locale.US).format(Date(millis))
                        val dayAbbr = when (dayOfWeek) {
                            "Mon" -> "MO"
                            "Tue" -> "TU"
                            "Wed" -> "WE"
                            "Thu" -> "TH"
                            "Fri" -> "FR"
                            "Sat" -> "SA"
                            "Sun" -> "SU"
                            else -> ""
                        }
                        onDateSelected(date, dayAbbr)
                    }
                    onDismiss()
                }
            ) {
                Text("OK", color = calendarPickerMainColor)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = calendarPickerMainColor)
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                selectedDayContainerColor = calendarPickerMainColor,
                selectedDayContentColor = Color.White,
                selectedYearContainerColor = calendarPickerMainColor,
                selectedYearContentColor = Color.White,
                todayContentColor = calendarPickerMainColor,
                todayDateBorderColor = calendarPickerMainColor
            ),
            dateValidator = { day ->
                // Check if the selected day is not Sunday
                val dayOfWeek = Calendar.getInstance().apply { timeInMillis = day }.get(Calendar.DAY_OF_WEEK)
                dayOfWeek != Calendar.SUNDAY
            }
        )
    }
}

fun Long.convertMillisToDate(): String {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = this@convertMillisToDate
        val zoneOffset = get(Calendar.ZONE_OFFSET)
        val dstOffset = get(Calendar.DST_OFFSET)
        add(Calendar.MILLISECOND, -(zoneOffset + dstOffset))
    }
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.US)
    return sdf.format(calendar.time)
}