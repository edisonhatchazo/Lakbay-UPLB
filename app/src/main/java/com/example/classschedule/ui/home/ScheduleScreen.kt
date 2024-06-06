@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.classschedule.ui.home

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.classschedule.R
import com.example.classschedule.ui.AppViewModelProvider
import com.example.classschedule.ui.navigation.NavigationDestination
import com.example.classschedule.ui.theme.getColorEntry
import java.time.LocalTime

object ScheduleHomeDestination: NavigationDestination {
    override val route = "schedule_home"
    override val titleRes = R.string.app_name
}

@Composable
fun ScheduleScreen(
    scheduleViewModel: ScheduleViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateToScheduleEntry: () -> Unit,
    modifier: Modifier = Modifier,
    navigateToScheduleUpdate: (Int) -> Unit
){
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ScheduleTopAppBar(
                title = stringResource(R.string.app_name),
                canNavigateBack = false,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToScheduleEntry,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription =  stringResource(R.string.class_entry_title)
                )
            }
        },
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
    scheduleViewModel: ScheduleViewModel = viewModel(factory = AppViewModelProvider.Factory),
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val classHomeUiState by scheduleViewModel.classHomeUiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        // Headers
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "",
                modifier = Modifier.width(40.dp),
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
                            .width(40.dp)
                            .padding(end = 4.dp),
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
                        val borderColor = classForThisTime?.let { getColorEntry(it.colorName).backgroundColor } ?: Color.Black
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(20.dp)
                                .border(0.5.dp, borderColor, RectangleShape) // Set border color dynamically
                                .background(color = classForThisTime?.let { getColorEntry(it.colorName).backgroundColor } ?: Color.Transparent)
                                .padding(2.dp)
                                .clickable {
                                    classForThisTime?.let { navigateToScheduleUpdate(it.id) }
                                },
                            contentAlignment = Alignment.Center // Centering the content
                        ) {
                            if (classForThisTime != null && classForThisTime.time == timeSlotStart) {
                                Text(
                                    text = classForThisTime.title,
                                    color = getColorEntry(classForThisTime.colorName).fontColor,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,

                                )
                            }
                        }
                    }
                }
            }
        }
    }
}