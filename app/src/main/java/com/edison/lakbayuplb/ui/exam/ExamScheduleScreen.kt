package com.edison.lakbayuplb.ui.exam

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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edison.lakbayuplb.R
import com.edison.lakbayuplb.ui.navigation.AppViewModelProvider
import com.edison.lakbayuplb.ui.navigation.NavigationDestination
import com.edison.lakbayuplb.ui.screen.ExamScheduleScreenTopAppBar
import com.edison.lakbayuplb.ui.settings.global.TopAppBarColorSchemesViewModel
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
    openDrawer: () -> Unit,
    colorViewModel: TopAppBarColorSchemesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val topAppBarColors = colorViewModel.topAppBarColors.collectAsState()
    val (topAppBarBackgroundColor, topAppBarForegroundColor) = topAppBarColors.value

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
                examDates = examDates,
                selectedDate = selectedDate,
                openDrawer = openDrawer,
                onDateSelected = { date -> selectedDate = date },
                topAppBarForegroundColor = topAppBarForegroundColor,
                topAppBarBackgroundColor = topAppBarBackgroundColor
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
    val colorSchemes by examScheduleViewModel.colorSchemes.collectAsState()

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
                        val borderColor = examForThisTime?.let { colorSchemes[it.colorId]?.backgroundColor }
                            ?: MaterialTheme.colorScheme.onSurface
                        val backgroundColor = examForThisTime?.let { colorSchemes[it.colorId]?.backgroundColor }
                            ?: MaterialTheme.colorScheme.background
                        val fontColor = examForThisTime?.let { colorSchemes[it.colorId]?.fontColor }
                            ?: MaterialTheme.colorScheme.onSurface

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(19.dp)
                                .border(0.5.dp, borderColor, RectangleShape) // Set border color dynamically
                                .background(color = backgroundColor)
                                .padding(0.5.dp)
                                .clickable {
                                    examForThisTime?.let { navigateToScheduleUpdate(it.id) }
                                },
                            contentAlignment = Alignment.Center // Centering the content

                        ) {
                            if (examForThisTime != null && examForThisTime.time == timeSlotStart) {
                                Text(
                                    text = examForThisTime.title,
                                    color = fontColor,
                                    fontSize = 10.sp,
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