package com.edison.lakbayuplb.ui.navigation

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.edison.lakbayuplb.R
import com.edison.lakbayuplb.ui.screen.Screen

@Composable
fun NavigationDrawerContent(navController: NavHostController, closeDrawer: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(240.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(top = 10.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.lakbay_uplb),  // Use your app icon resource
                    contentDescription = null,
                    modifier = Modifier.size(70.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Image(
                    painter = painterResource(id = R.drawable.lakbay_uplb_text),  // Use your app icon resource
                    contentDescription = null,
                    modifier = Modifier
                        .size(110.dp)
                        .padding(bottom = 20.dp)
                )
            }

            Divider()

            // Navigation items
            DrawerDropdownMenu(
                iconResId = R.drawable.icons8_class_18___,
                title = "Classes",
                items = listOf(
                    DrawerMenuItem("Class Schedule Screen", Icons.Filled.DateRange) {
                        navController.navigate(Screen.Schedules.route)
                        closeDrawer()
                    },
                    DrawerMenuItem("Classes Screen", Icons.Filled.List) {
                        navController.navigate(Screen.Classes.route)
                        closeDrawer()
                    }
                )
            )
            DrawerDropdownMenu(
                iconResId = R.mipmap.blue_book,
                title = "Exams",
                items = listOf(
                    DrawerMenuItem("Exams Schedule Screen", Icons.Filled.DateRange) {
                        navController.navigate(Screen.ExamSchedule.route)
                        closeDrawer()
                    },
                    DrawerMenuItem("Exams Screen", Icons.Filled.List) {
                        navController.navigate(Screen.Exams.route)
                        closeDrawer()
                    }
                )
            )
            DrawerDropdownMenu(
                iconResId = R.drawable.icons8_school_building_18___,
                title = "Location",
                items = listOf(
                    DrawerMenuItem("Buildings Screen", Icons.Filled.List) {
                        navController.navigate(Screen.Buildings.route)
                        closeDrawer()
                    },
                    DrawerMenuItem("My Own Pins Screen", Icons.Filled.LocationOn) {
                        navController.navigate(Screen.Pins.route)
                        closeDrawer()
                    }
                )
            )

            TextButton(
                onClick = {
                    navController.navigate(Screen.Map.route)
                    closeDrawer()
                },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(0.dp)  // Remove padding to align text to the start
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(R.mipmap.map_icon),
                        contentDescription = null,  // Provide a content description for accessibility
                        modifier = Modifier.size(24.dp),  // Adjust the size as needed
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Map", modifier = Modifier.weight(1f))
                }
            }

            TextButton(
                onClick = {
                    navController.navigate(Screen.Settings.route)
                    closeDrawer()
                },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(0.dp)  // Remove padding to align text to the start
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Settings", modifier = Modifier.weight(1f))
                }
            }

            TextButton(
                onClick = {
                    closeDrawer()
                },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(0.dp)  // Remove padding to align text to the start
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("About", modifier = Modifier.weight(1f))
                }
            }
        }

        IconButton(
            onClick = closeDrawer,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 32.dp)  // Add top padding
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}


@Composable
fun DrawerDropdownMenu(
    iconResId: Int,
    title: String,
    items: List<DrawerMenuItem>,
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
                Image(
                    painter = painterResource(id = iconResId),
                    contentDescription = null,  // Provide a content description for accessibility
                    modifier = Modifier.size(24.dp) , // Adjust the size as needed
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(title, modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.ArrowDropDown,
                    contentDescription = null
                )
            }
        }
        if (expanded) {
            Column(modifier = Modifier.padding(start = 16.dp)) {
                items.forEach { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                item.onClick()
                                expanded = false
                            }
                            .padding(vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(item.title, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}


data class DrawerMenuItem(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)