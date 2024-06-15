package com.example.classschedule.algorithm

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
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
                            "Mon" -> "MON"
                            "Tue" -> "TUE"
                            "Wed" -> "WED"
                            "Thu" -> "THU"
                            "Fri" -> "FRI"
                            "Sat" -> "SAT"
                            "Sun" -> "SUN"
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

@Composable
fun CustomDatePickerDialog(
    initialDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
    examDates: List<LocalDate>
) {
    var currentDate by remember { mutableStateOf(initialDate) }

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(shape = MaterialTheme.shapes.medium, color = Color.White) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Select date")

                // Month and Year Navigation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        currentDate = currentDate.minusMonths(1)
                    }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Previous Month")
                    }
                    Text(
                        text = "${currentDate.month.name} ${currentDate.year}",
                        textAlign = TextAlign.Center
                    )
                    IconButton(onClick = {
                        currentDate = currentDate.plusMonths(1)
                    }) {
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Next Month")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Calendar View
                CalendarView(
                    selectedDate = currentDate,
                    onDateSelected = { date -> currentDate = date },
                    examDates = examDates
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = { onDismiss() }) {
                        Text("Cancel")
                    }
                    TextButton(onClick = {
                        onDateSelected(currentDate)
                        onDismiss()
                    }) {
                        Text("OK")
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarView(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    examDates: List<LocalDate>
) {
    val currentMonth = selectedDate.month
    val daysInMonth = currentMonth.length(selectedDate.isLeapYear)
    val firstDayOfMonth = selectedDate.withDayOfMonth(1).dayOfWeek.value

    Column {
        // Days of the week
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Calendar days
        val weeks = (daysInMonth + firstDayOfMonth - 1) / 7 + 1
        for (week in 0 until weeks) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (day in 0..6) {
                    val dayOfMonth = week * 7 + day - firstDayOfMonth + 1
                    val date = selectedDate.withDayOfMonth(dayOfMonth.coerceIn(1, daysInMonth))
                    val isSelected = date == selectedDate
                    val hasExam = examDates.contains(date)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .background(
                                if (isSelected) Color.Cyan
                                else if (hasExam) Color.Yellow
                                else Color.Transparent)
                            .clickable {
                                if (dayOfMonth in 1..daysInMonth) onDateSelected(date)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = if (dayOfMonth in 1..daysInMonth) dayOfMonth.toString() else "")
                    }
                }
            }
        }
    }
}