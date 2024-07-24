package com.example.classschedule.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.classschedule.R
import com.example.classschedule.ui.screen.Screen
import kotlinx.coroutines.launch

@Composable
fun NavigationDrawerContent(navController: NavHostController, closeDrawer: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(240.dp)  // Fixed width for the drawer content
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        // Header with app icon, title, and email
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 40.dp),
        ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),  // Use your app icon resource
            contentDescription = null,
            modifier = Modifier.size(40.dp)

        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text("Android Studio")
            Text("android.studio@android.com")
        }
    }

        Divider()

        // Navigation items
        DrawerDropdownMenu(
            title = "Classes",
            items = listOf(
                "Class Schedule Screen" to {
                    navController.navigate(Screen.Schedules.route)
                    closeDrawer()
                },
                "Classes Screen" to {
                    navController.navigate(Screen.Classes.route)
                    closeDrawer()
                }
            )
        )
        DrawerDropdownMenu(
            title = "Exams",
            items = listOf(
                "Exams Schedule Screen" to {
                    navController.navigate(Screen.ExamSchedule.route)
                    closeDrawer()
                },
                "Exams Screen" to {
                    navController.navigate(Screen.Exams.route)
                    closeDrawer()
                }
            )
        )
        DrawerDropdownMenu(
            title = "Location",
            items = listOf(
                "Buildings Screen" to {
                    navController.navigate(Screen.Buildings.route)
                    closeDrawer()
                },
                "My Own Pins Screen" to {
                    navController.navigate(Screen.Pins.route)
                    closeDrawer()
                }
            )
        )
        TextButton(onClick = {
            navController.navigate(Screen.Map.route)
            closeDrawer() }
        ) {
            Text("Map")
        }
        TextButton(onClick = {
            navController.navigate(Screen.Settings.route)
            closeDrawer() }) {
            Text("Settings")
        }
        TextButton(onClick = { closeDrawer() }) {
            Text("About")
        }
    }
}

@Composable
fun DrawerDropdownMenu(
    title: String,
    items: List<Pair<String, () -> Unit>>,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.background(MaterialTheme.colorScheme.surface)) {
        TextButton(
            onClick = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(0.dp)  // Remove padding to align text to the start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(title, modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.ArrowDropDown,
                    contentDescription = null
                )
            }
        }
        if (expanded) {
            Column(modifier = Modifier.padding(start = 16.dp)) {
                items.forEach { (itemTitle, onClick) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onClick()
                                expanded = false
                            }
                            .padding(vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,  // Replace with appropriate icons
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(itemTitle)
                    }
                }
            }
        }
    }
}


@Composable
fun MyApp() {
    val mainNavController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = false,
        drawerContent = {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(240.dp)  // Fixed width for the drawer content
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                NavigationDrawerContent(
                    navController = mainNavController,
                    closeDrawer = { coroutineScope.launch { drawerState.close() } }
                )
            }
        },
        content = {
            Navigation(
                navController = mainNavController,
                openDrawer = { coroutineScope.launch { drawerState.open() } }
            )
        }
    )
}