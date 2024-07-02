package com.example.classschedule.ui.exam

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.classschedule.R
import com.example.classschedule.ui.navigation.AppViewModelProvider
import com.example.classschedule.ui.navigation.NavigationDestination
import com.example.classschedule.ui.screen.ExamScheduleScreenTopAppBar
import com.example.classschedule.ui.theme.ColorPalette
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object ExamScheduleDestination: NavigationDestination {
    override val route = "exam_schedule_home"
    override val titleRes = R.string.exam_schedule
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScheduleScreen(
    modifier: Modifier = Modifier,
    examScheduleViewModel: ExamHomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateToScheduleEntry: () -> Unit,
    navigateToScheduleUpdate: (Int) -> Unit,
    navigateToScheduleHomeDestination: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val examDates = examScheduleViewModel.examHomeUiState.collectAsState().value.examScheduleList.map {
        LocalDate.parse(it.date, DateTimeFormatter.ofPattern("MMM dd, yyyy"))
    }
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ExamScheduleScreenTopAppBar(
                title = stringResource(R.string.exam_schedule),
                canNavigateBack = false,
                navigateToScheduleEntry = navigateToScheduleEntry,
                navigateToClassHome = navigateToScheduleHomeDestination,
                navigateToExamHome = {},
                examDates = examDates,
                selectedDate = selectedDate,
                onDateSelected = { date -> selectedDate = date }
            )
        }
    ) { innerPadding ->
        ExamScheduleScreenBody(
            navigateToScheduleUpdate = navigateToScheduleUpdate,
            examScheduleViewModel = examScheduleViewModel,
            contentPadding = innerPadding,
            selectedDate = selectedDate
        )
    }
}

@Composable
fun ExamScheduleScreenBody(
    navigateToScheduleUpdate: (Int) -> Unit,
    examScheduleViewModel: ExamHomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    selectedDate: LocalDate
) {
    val examHomeUiState by examScheduleViewModel.examHomeUiState.collectAsState()
    val startOfWeek = selectedDate.with(DayOfWeek.MONDAY)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {

        // Month Header
        Text(
            text = selectedDate.month.name,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            textAlign = TextAlign.Center
        )

        // Headers with day and dates together
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "",
                modifier = Modifier.width(50.dp),
                textAlign = TextAlign.Center,
                fontSize = 14.sp
            )
            for (dayOffset in 0..5) {
                val currentDay = startOfWeek.plusDays(dayOffset.toLong())
                Text(
                    text = "${currentDay.dayOfWeek.name.take(1)} (${currentDay.dayOfMonth})",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )
            }
        }

        // Time rows in 30-minute intervals from 7 AM to 9 PM
        for (hour in 7..20) {
            for (half in 0..1) {
                val timeLabel = if (half == 0) "$hour:00" else "$hour:30"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = timeLabel,
                        modifier = Modifier
                            .width(50.dp)
                            .padding(end = 4.dp)
                            .offset(y = (-8).dp),
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp
                    )
                    for (dayOffset in 0..5) {
                        val currentDay = startOfWeek.plusDays(dayOffset.toLong())
                        val timeSlotStart = LocalTime.of(hour, half * 30)
                        val timeSlotEnd = timeSlotStart.plusMinutes(30)
                        val examForThisTime = examHomeUiState.examScheduleList.firstOrNull {
                            it.day == currentDay.dayOfWeek.name.take(3) && // Check if the day matches
                                    LocalDate.parse(it.date, DateTimeFormatter.ofPattern("MMM dd, yyyy")) == currentDay && // Check if the date matches
                                    (it.time.isBefore(timeSlotEnd) && it.timeEnd.isAfter(timeSlotStart))
                        }
                        val borderColor = examForThisTime?.let { ColorPalette.getColorEntry(it.colorName).backgroundColor } ?: Color.Black
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(19.dp)
                                .border(0.5.dp, borderColor, RectangleShape
                                ) // Set border color dynamically
                                .background(color = examForThisTime?.let {
                                    ColorPalette.getColorEntry(
                                        it.colorName
                                    ).backgroundColor
                                } ?: Color.Transparent)
                                .padding(2.dp)
                                .clickable {
                                    examForThisTime?.let { navigateToScheduleUpdate(it.id) }
                                },
                            contentAlignment = Alignment.Center // Centering the content

                        ) {
                            if (examForThisTime != null && examForThisTime.time == timeSlotStart) {
                                Text(
                                    text = examForThisTime.title,
                                    color = ColorPalette.getColorEntry(examForThisTime.colorName).fontColor,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)  // Ensure the row is tall enough for the text
        ) {
            Text(
                text = "21:00",
                modifier = Modifier
                    .width(50.dp)
                    .padding(end = 4.dp)
                    .offset(y = (-8).dp),
                textAlign = TextAlign.Center,
                fontSize = 12.sp
            )
        }
    }
}