package com.edison.lakbayuplb.ui.classes

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edison.lakbayuplb.R
import com.edison.lakbayuplb.ui.navigation.AppViewModelProvider
import com.edison.lakbayuplb.ui.navigation.NavigationDestination
import com.edison.lakbayuplb.ui.screen.ScheduleScreenTopAppBar
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
        )
    }
}

@Composable
fun ScheduleScreenBody(
    navigateToScheduleUpdate: (Int) -> Unit,
    scheduleViewModel: ClassHomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val classHomeUiState by scheduleViewModel.classHomeUiState.collectAsState()
    val colorSchemes by scheduleViewModel.colorSchemes.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
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

        // Time rows in 30-minute intervals from 7 AM to 7 PM
        for (hour in 7..18) {
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
                    for (day in listOf("M", "T", "W", "TH", "F", "S")) {
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
                                .border(0.5.dp, borderColor, RectangleShape) // Set border color dynamically
                                .background(color = backgroundColor)
                                .padding(0.5.dp)
                                .clickable {
                                    classForThisTime?.let { navigateToScheduleUpdate(it.id) }
                                },
                            contentAlignment = Alignment.Center // Centering the content

                        ) {
                            if (classForThisTime != null && classForThisTime.time == timeSlotStart) {
                                Text(
                                    text = classForThisTime.title,
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
                text = "19:00",
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