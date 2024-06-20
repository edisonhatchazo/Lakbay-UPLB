package com.example.classschedule.ui.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.classschedule.R
import com.example.classschedule.algorithm.CustomDatePickerDialog
import com.google.maps.android.compose.MapType
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleEntryScreenTopAppBar(
    title:String,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit = {},
){
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Blue),
        title = { Text( title,color = Color.White)},
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if(canNavigateBack){
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription  = stringResource(R.string.back_button),
                        tint = Color.White
                    )
                }
            }
        },
        actions = {

            IconButton(
                onClick = { /*TODO*/ }
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = stringResource(R.string.about),
                    tint = Color.White
                )
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreenTopAppBar(
    title:String,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    navigateToScheduleEntry: () -> Unit,
    navigateToClassHome: () -> Unit,
    navigateToExamHome: () -> Unit,
    navigateUp: () -> Unit = {},

){
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var showMenu  by remember { mutableStateOf(false) }
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Blue),
        title = { Text( title,color = Color.White)},
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if(canNavigateBack){
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription  = stringResource(R.string.back_button),
                        tint = Color.White
                    )
                }
            }
        },
        actions = {
            IconButton(
                onClick = { showMenu = !showMenu}
            ){
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Menu",
                    tint = Color.White
                )
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    onClick = {
                        navigateToClassHome()
                        showMenu = false
                    },
                    text = {Text("Class")}
                )
                DropdownMenuItem(
                    onClick = {
                        navigateToExamHome()
                        showMenu = false
                    },
                    text = {Text("Exam")}
                )
            }

            IconButton(
                onClick = navigateToScheduleEntry
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription =  stringResource(R.string.class_entry_title),
                    tint = Color.Yellow
                )
            }

            IconButton(
                onClick = { /*TODO*/ }
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = stringResource(R.string.about),
                    tint = Color.White
                )
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScheduleScreenTopAppBar(
    title:String,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    navigateToScheduleEntry: () -> Unit,
    navigateToClassHome: () -> Unit,
    navigateToExamHome: () -> Unit,
    navigateUp: () -> Unit = {},
    examDates: List<LocalDate>,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
){
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var showMenu  by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Blue),
        title = { Text( title,color = Color.White)},
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if(canNavigateBack){
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription  = stringResource(R.string.back_button),
                        tint = Color.White
                    )
                }
            }
        },
        actions = {
            IconButton(
                onClick = { showMenu = !showMenu}
            ){
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Menu",
                    tint = Color.White
                )
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    onClick = {
                        navigateToClassHome()
                        showMenu = false
                    },
                    text = {Text("Class")}
                )
                DropdownMenuItem(
                    onClick = {
                        navigateToExamHome()
                        showMenu = false
                    },
                    text = {Text("Exam")}
                )
            }

            IconButton(
                onClick = navigateToScheduleEntry
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription =  stringResource(R.string.class_entry_title),
                    tint = Color.Yellow
                )
            }

            IconButton(
                onClick = { showDatePicker = true }
            ) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = stringResource(R.string.date_select),
                    tint = Color.White
                )
            }
            if (showDatePicker) {
                CustomDatePickerDialog(
                    initialDate = selectedDate,
                    onDateSelected = { date ->
                        onDateSelected(date)
                        showDatePicker = false
                    },
                    onDismiss = { showDatePicker = false },
                    examDates = examDates
                )
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreenTopAppBar(
    title:String,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit = {},
){
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Blue),
        title = { Text( title,color = Color.White)},
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if(canNavigateBack){
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription  = stringResource(R.string.back_button),
                        tint = Color.White
                    )
                }
            }
        },
        actions = {

            IconButton(
                onClick = {/* To Do Later*/}
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = stringResource(R.string.about),
                    tint = Color.White
                )
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildingsScreenTopAppBar(
    title:String,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    navigateToPinsHome: () -> Unit,
    navigateUp: () -> Unit = {},

    ){
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var showMenu  by remember { mutableStateOf(false) }
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Blue),
        title = { Text( title,color = Color.White)},
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if(canNavigateBack){
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription  = stringResource(R.string.back_button),
                        tint = Color.White
                    )
                }
            }
        },
        actions = {
            IconButton(
                onClick = { showMenu = !showMenu}
            ){
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Menu",
                    tint = Color.White
                )
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    onClick = {
                        showMenu = false
                    },
                    text = {Text("UPLB Buildings")}
                )
                DropdownMenuItem(
                    onClick = {
                        navigateToPinsHome()
                        showMenu = false
                    },
                    text = {Text("My Pins")}
                )
            }

            IconButton(
                onClick = { /*TODO*/ }
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = stringResource(R.string.about),
                    tint = Color.White
                )
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinsScreenTopAppBar(
    title:String,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    navigateToPinEntry: () -> Unit,
    navigateToBuildingHome: () -> Unit,
    navigateUp: () -> Unit = {},
    ){
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var showMenu  by remember { mutableStateOf(false) }
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Blue),
        title = { Text( title,color = Color.White)},
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if(canNavigateBack){
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription  = stringResource(R.string.back_button),
                        tint = Color.White
                    )
                }
            }
        },
        actions = {
            IconButton(
                onClick = { showMenu = !showMenu}
            ){
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Menu",
                    tint = Color.White
                )
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    onClick = {
                        navigateToBuildingHome()
                        showMenu = false
                    },
                    text = {Text("UPLB Buildings")}
                )
                DropdownMenuItem(
                    onClick = {
                        showMenu = false
                    },
                    text = {Text("My Pins")}
                )
            }

            IconButton(
                onClick = navigateToPinEntry
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription =  stringResource(R.string.pin_entry_title),
                    tint = Color.Yellow
                )
            }

            IconButton(
                onClick = { /*TODO*/ }
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = stringResource(R.string.about),
                    tint = Color.White
                )
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoordinateEntryScreenTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit = {},
    onMapTypeChange: (MapType) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var expanded by remember { mutableStateOf(false) }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Blue),
        title = { Text(title, color = Color.White) },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button),
                        tint = Color.White
                    )
                }
            }
        },
        actions = {
            IconButton(
                onClick = { /*TODO*/ }
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = stringResource(R.string.about),
                    tint = Color.White
                )
            }

            IconButton(onClick = { expanded = true }) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = stringResource(R.string.map_type),
                    tint = Color.White
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    CustomMapType.entries.forEach { mapType ->
                        DropdownMenuItem(
                            onClick = {
                                onMapTypeChange(mapType.mapType)
                                expanded = false
                            },
                            text = {Text(mapType.displayName)}
                        )
                    }
                }
            }
        },
    )
}