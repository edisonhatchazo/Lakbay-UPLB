package com.example.classschedule.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.classschedule.R
import com.example.classschedule.algorithm.CustomDatePickerDialog
import com.example.classschedule.algorithm.SearchViewModel
import com.example.classschedule.ui.navigation.AppViewModelProvider
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleEntryScreenTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit = {},
){
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Blue),
        title = { Text( title,color = Color.White)},
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
    title: String,
    canNavigateBack: Boolean,
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
    title: String,
    canNavigateBack: Boolean,
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
    title: String,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit = {},
){
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Blue),
        title = { Text( title,color = Color.White)},
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
    title: String,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    navigateToPinsHome: () -> Unit,
    navigateToRoomDetails: (Int) -> Unit,
    navigateToBuildingDetails: (Int) -> Unit,
    navigateUp: () -> Unit = {},
    searchViewModel: SearchViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var showMenu by remember { mutableStateOf(false) }
    var showSearchBar by remember { mutableStateOf(false) }
    val buildingSuggestions by searchViewModel.buildingSuggestions.collectAsState()
    val roomSuggestions by searchViewModel.roomSuggestions.collectAsState()

    Column {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Blue),
            title = {
                if (showSearchBar) {
                    TextField(
                        value = searchViewModel.searchQuery,
                        onValueChange = { query -> searchViewModel.updateSearchQuery(query) },
                        placeholder = { Text("Search...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(title, color = Color.White)
                }
            },
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
                if (showSearchBar) {
                    IconButton(onClick = { showSearchBar = false }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.close_search),
                            tint = Color.White
                        )
                    }
                } else {
                    IconButton(onClick = { showSearchBar = true }) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = stringResource(R.string.search),
                            tint = Color.White
                        )
                    }
                }
                IconButton(
                    onClick = { showMenu = !showMenu }
                ) {
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
                        text = { Text("UPLB Buildings") }
                    )
                    DropdownMenuItem(
                        onClick = {
                            navigateToPinsHome()
                            showMenu = false
                        },
                        text = { Text("My Pins") }
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

        if (showSearchBar && (buildingSuggestions.isNotEmpty() || roomSuggestions.isNotEmpty())) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .heightIn(max = 400.dp) // Adjust height as needed
            ) {
                LazyColumn {
                    if (buildingSuggestions.isNotEmpty()) {
                        item {
                            Text(
                                text = "Buildings",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                        items(buildingSuggestions) { building ->
                            DropdownMenuItem(
                                onClick = {
                                    navigateToBuildingDetails(building.buildingId)
                                    showSearchBar = false
                                },
                                text = { Text(building.name) }
                            )
                        }
                    }
                    if (roomSuggestions.isNotEmpty()) {
                        item {
                            Text(
                                text = "Rooms",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                        items(roomSuggestions) { room ->
                            DropdownMenuItem(
                                onClick = {
                                    navigateToRoomDetails(room.roomId)
                                    showSearchBar = false
                                },
                                text = { Text(room.title) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinsScreenTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    navigateToPinEntry: () -> Unit,
    navigateToBuildingHome: () -> Unit,
    navigateUp: () -> Unit = {},
    ){
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var showMenu  by remember { mutableStateOf(false) }
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Blue),
        title = { Text( title,color = Color.White)},
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
    navigateUp: () -> Unit = {}
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var expanded by remember { mutableStateOf(false) }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Blue),
        title = { Text(title, color = Color.White) },
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

        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreenTopAppBar(
    title: String,
    onGetDirectionsClick: () -> Unit,
    onRouteTypeSelected: (String) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val carIcon: Painter = painterResource(id = R.drawable.car_icon)
    val cyclingIcon: Painter = painterResource(id = R.drawable.cycling_icon)
    val walkingIcon: Painter = painterResource(id = R.drawable.walking_icon)
    val transitIcon: Painter = painterResource(id = R.mipmap.transit)
    var expanded by remember { mutableStateOf(false) }
    var selectedIcon by remember { mutableStateOf(walkingIcon) }
    var selectedRouteType by remember { mutableStateOf("walking") }
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Blue),
        title = { Text(title, color = Color.White) },
        scrollBehavior = scrollBehavior,
        actions = {
            IconButton(onClick = onGetDirectionsClick) {
                Icon(
                    imageVector = Icons.Filled.Place,
                    contentDescription = stringResource(R.string.about),
                    tint = Color.White
                )
            }
            Box {
                IconButton(onClick = { expanded = true }) {
                    Image(
                        painter = selectedIcon,
                        contentDescription = "Route Icon",
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(onClick = {
                        selectedIcon = walkingIcon
                        selectedRouteType = "foot"
                        onRouteTypeSelected(selectedRouteType)
                        expanded = false
                    }, text = {Text("Walking Route")}
                    )
                    DropdownMenuItem(onClick = {
                        selectedIcon = cyclingIcon
                        selectedRouteType = "bicycle"
                        onRouteTypeSelected(selectedRouteType)
                        expanded = false
                    }, text = {Text("Cycling Route")}
                    )
                    DropdownMenuItem(onClick = {
                        selectedIcon = carIcon
                        selectedRouteType = "driving"
                        onRouteTypeSelected(selectedRouteType)
                        expanded = false
                    }, text = {Text("Car Route")}
                        )

                    DropdownMenuItem(onClick = {
                        selectedIcon = transitIcon
                        selectedRouteType = "transit"
                        onRouteTypeSelected(selectedRouteType)
                        expanded = false
                    },text = {Text("Transit Route")}
                        )
                }
            }
        }
    )
}