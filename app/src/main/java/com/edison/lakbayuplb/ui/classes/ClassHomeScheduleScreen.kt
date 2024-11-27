package com.edison.lakbayuplb.ui.classes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edison.lakbayuplb.R
import com.edison.lakbayuplb.algorithm.generateScheduleSlots
import com.edison.lakbayuplb.ui.navigation.AppViewModelProvider
import com.edison.lakbayuplb.ui.navigation.NavigationDestination
import com.edison.lakbayuplb.ui.screen.ScheduleScreenTopAppBar
import com.edison.lakbayuplb.ui.settings.global.AppPreferences
import com.edison.lakbayuplb.ui.settings.global.TopAppBarColorSchemesViewModel
import com.edison.lakbayuplb.ui.theme.ColorPaletteViewModel
import java.time.LocalTime

object ClassHomeScheduleDestination: NavigationDestination {
    override val route = "schedule_home"
    override val titleRes = R.string.class_schedule
}

@Composable
fun ClassScheduleHomeScreen(
    modifier: Modifier = Modifier,
    scheduleViewModel: ClassHomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateToScheduleEntry: () -> Unit,
    navigateToScheduleUpdate: (Int) -> Unit,
    openDrawer: () -> Unit,
    colorPaletteViewModel: ColorPaletteViewModel = viewModel(factory = AppViewModelProvider.Factory),
    colorViewModel: TopAppBarColorSchemesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val topAppBarColors = colorViewModel.topAppBarColors.collectAsState()
    val (topAppBarBackgroundColor, topAppBarForegroundColor) = topAppBarColors.value

    val context = LocalContext.current
    val appPreferences = AppPreferences(context)
    val fontSize = appPreferences.getFontSize()
    LaunchedEffect(Unit){
        colorPaletteViewModel.observeColorSchemes()
    }
    Scaffold(
        modifier = modifier,
        topBar = {
            ScheduleScreenTopAppBar(
                title = stringResource(R.string.class_schedule),
                canNavigateBack = false,
                navigateToScheduleEntry = navigateToScheduleEntry,
                openDrawer = openDrawer,
                topAppBarBackgroundColor = topAppBarBackgroundColor,
                topAppBarForegroundColor = topAppBarForegroundColor

            )
        }
    ){ innerPadding ->
        ScheduleScreenBody(
            navigateToScheduleUpdate = navigateToScheduleUpdate,
            scheduleViewModel = scheduleViewModel,
            contentPadding = innerPadding,
            fontSize = fontSize
        )
    }
}
@Composable
fun ScheduleScreenBody(
    navigateToScheduleUpdate: (Int) -> Unit,
    scheduleViewModel: ClassHomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    fontSize: Float
) {

    val classHomeUiState by scheduleViewModel.classHomeUiState.collectAsState()
    val colorSchemes by scheduleViewModel.colorSchemes.collectAsState()

    val classSlotsMap = HashMap<String, MutableList<String>>()

    classHomeUiState.classScheduleList.forEach { classSchedule ->
        val key = "${classSchedule.title} ${classSchedule.section}"
        val slots = generateScheduleSlots( // Generates the Information that will be shown in the schedule
            classSchedule.title,
            classSchedule.section,
            classSchedule.time,
            classSchedule.timeEnd,
            classSchedule.days
        )
        classSlotsMap[key] = slots
    }

    // Make the entire Column scrollable
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        // Add header as the first item in the LazyColumn
        item {
            // Headers
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "",
                    modifier = Modifier.width(50.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )
                listOf("M", "T", "W", "TH", "F", "S").forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Add time slots for 7:00 AM to 12:00 PM
        items((7..12).toList()) { hour ->  // Convert the IntRange to a List
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
                    listOf("M", "T", "W", "TH", "F", "S").forEach { day ->
                        val timeSlotStart = LocalTime.of(hour, half * 30)
                        val timeSlotEnd = timeSlotStart.plusMinutes(30)
                        val classForThisTime = classHomeUiState.classScheduleList.firstOrNull {
                            it.days.split(", ").contains(day) && // Check if the day is in the days list
                                    (it.time.isBefore(timeSlotEnd) && it.timeEnd.isAfter(timeSlotStart))
                        }
                        val borderColor = classForThisTime?.let { colorSchemes[it.colorId]?.backgroundColor }
                            ?: MaterialTheme.colorScheme.onSurface
                        val backgroundColor = classForThisTime?.let { colorSchemes[it.colorId]?.backgroundColor }
                            ?: MaterialTheme.colorScheme.background
                        val fontColor = classForThisTime?.let { colorSchemes[it.colorId]?.fontColor }
                            ?: MaterialTheme.colorScheme.onSurface

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(20.dp)
                                .border(
                                    0.5.dp,
                                    borderColor,
                                    RectangleShape
                                ) // Set border color dynamically
                                .background(color = backgroundColor)
                                .padding(0.5.dp)
                                .clickable {
                                    classForThisTime?.let { navigateToScheduleUpdate(it.id) }
                                },
                            contentAlignment = Alignment.Center // Centering the content
                        ) {
                            if (classForThisTime != null) {
                                val slots = classSlotsMap["${classForThisTime.title} ${classForThisTime.section}"]
                                if (!slots.isNullOrEmpty()) {
                                    val currentText = slots[0]
                                    slots.removeAt(0)
                                    Text(
                                        text = currentText,
                                        color = fontColor,
                                        fontSize = fontSize.sp,
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Add time slots for 1:00 PM to 7:00 PM
        items((1..6).toList()) { hour ->  // Convert the IntRange to a List
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
                    listOf("M", "T", "W", "TH", "F", "S").forEach { day ->
                        val timeSlotStart = LocalTime.of(hour + 12, half * 30)  // Add 12 to shift the time to PM hours
                        val timeSlotEnd = timeSlotStart.plusMinutes(30)
                        val classForThisTime = classHomeUiState.classScheduleList.firstOrNull {
                            it.days.split(", ").contains(day) && // Check if the day is in the days list
                                    (it.time.isBefore(timeSlotEnd) && it.timeEnd.isAfter(timeSlotStart))
                        }
                        val borderColor = classForThisTime?.let { colorSchemes[it.colorId]?.backgroundColor }
                            ?: MaterialTheme.colorScheme.onSurface
                        val backgroundColor = classForThisTime?.let { colorSchemes[it.colorId]?.backgroundColor }
                            ?: MaterialTheme.colorScheme.background
                        val fontColor = classForThisTime?.let { colorSchemes[it.colorId]?.fontColor }
                            ?: MaterialTheme.colorScheme.onSurface

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(20.dp)
                                .border(
                                    0.5.dp,
                                    borderColor,
                                    RectangleShape
                                ) // Set border color dynamically
                                .background(color = backgroundColor)
                                .padding(0.5.dp)
                                .clickable {
                                    classForThisTime?.let { navigateToScheduleUpdate(it.id) }
                                },
                            contentAlignment = Alignment.Center // Centering the content
                        ) {
                            if (classForThisTime != null) {
                                val slots = classSlotsMap["${classForThisTime.title} ${classForThisTime.section}"]
                                if (!slots.isNullOrEmpty()) {
                                    val currentText = slots[0]
                                    slots.removeAt(0)
                                    Text(
                                        text = currentText,
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
        }

        // Extra slot for 7:00 PM
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)  // Ensure the row is tall enough for the text
            ) {
                Text(
                    text = "7:00",
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

}
