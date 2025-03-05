package com.edison.lakbayuplb.ui.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edison.lakbayuplb.R
import com.edison.lakbayuplb.ui.navigation.AppViewModelProvider
import com.edison.lakbayuplb.ui.navigation.NavigationDestination
import com.edison.lakbayuplb.ui.screen.AboutPageTopAppBar
import com.edison.lakbayuplb.ui.settings.global.TopAppBarColorSchemesViewModel
import com.edison.lakbayuplb.ui.theme.ThemeMode


object SettingsAndCustomizationDestination: NavigationDestination {
    override val route = "settings_and_customization"
    override val titleRes = R.string.settings_and_customization
}

@Composable
fun SettingsAndCustomization(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    colorViewModel: TopAppBarColorSchemesViewModel = viewModel(factory = AppViewModelProvider.Factory),
){
    val topAppBarColors = colorViewModel.topAppBarColors.collectAsState()
    val (topAppBarBackgroundColor, topAppBarForegroundColor) = topAppBarColors.value
    Scaffold(
        modifier = modifier,
        topBar = {
            AboutPageTopAppBar(
                title = stringResource(R.string.settings_and_customization),
                canNavigateBack = true,
                navigateUp = navigateBack,
                topAppBarBackgroundColor = topAppBarBackgroundColor,
                topAppBarForegroundColor = topAppBarForegroundColor,

            )
        }
    ){ innerPadding ->
        AboutSettingsAndCustomizationBody(innerPadding)
    }
}

@Composable
fun AboutSettingsAndCustomizationBody(
    innerPadding: PaddingValues = PaddingValues(0.dp),
){

    var showPopup by remember { mutableStateOf(false) }
    val selectedThemeMode by remember { mutableStateOf(ThemeMode.SYSTEM) }
    var url = R.drawable.lakbay_uplb_icon
    Card(
        modifier = Modifier.padding(innerPadding),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp, bottom = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF800000),
                    contentColor = Color.White
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            url = R.drawable.about_notifications
                            Text(
                                text = "Notifications",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold
                            )
                            IconButton(
                                onClick = { showPopup = true }
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.image_icon),
                                    contentDescription = "Notifications",
                                    modifier = Modifier.size(18.dp),
                                    colorFilter = ColorFilter.tint(
                                        if(selectedThemeMode == ThemeMode.DARK)
                                            MaterialTheme.colorScheme.onSurface else Color.White
                                    )
                                )
                            }
                        }
                        Text(
                            text = "Enable or disable notifications for class reminders, exam schedules, and navigation alerts.",
                            textAlign = TextAlign.Center
                        )
                        ClassesDescription(
                            url = R.drawable.class_exam_notification,
                            description = "Class & Exam Notifications",
                            text= "The app notifies you 1 hour before your class starts. If you're running late, it will alert you based on your estimated arrival time.\n " +
                                    "Lateness is calculated assuming a walking speed of 1 meter per second (m/s)—a slow but steady pace."
                        )
                        ClassesDescription(
                            url = R.drawable.navigation_notification,
                            description = "Navigation Alerts",
                            text= "Floating notifications appear on the screen to provide turn-by-turn directions while navigating. This ensures you stay on track without constantly checking the map."
                        )
                        ClassesDetailsDescription(
                            title = "Text-to-Speech Navigation",
                            description= "Voice guidance provides spoken turn directions, allowing for hands-free navigation. This feature helps users stay focused on their route while walking, cycling, or driving."
                        )
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp, bottom = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF800000),
                    contentColor = Color.White
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            url = R.drawable.about_notifications
                            Text(
                                text = "Setting Up Notifications (Outside the App)",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Text(
                            text = "To ensure you receive timely updates and alerts, configure your phone’s settings:",
                            textAlign = TextAlign.Center
                        )
                        JustifyDescription(
                            url = R.drawable.location_permission,
                            description = "Location Permission",
                            text= " • Set to: \"Allowed All the Time\"\n" +
                                  " •  Why? This allows Lakbay UPLB to track your location in the background for timely navigation alerts, class reminders, and late arrival notifications.\n" +
                                  " • How to Enable:\n" +
                                  "     1. Go to Settings > Apps > Lakbay UPLB\n" +
                                  "     2. Tap Permissions > Location\n" +
                                  "     3. Select \"Allow all the time\"")

                        JustifyDescription(
                            url = R.drawable.setting_notifications,
                            description = "Notifications Page",
                            text= "This page lets you customize how you receive notifications.\n"+
                                  " • How to Enable:\n" +
                                  "     1. Go to Settings > Apps > Lakbay UPLB\n" +
                                  "     2. Tap Permissions > Notifications"
                        )
                        JustifyDescription(
                            url = R.drawable.setting_notification,
                            description = "Notifications Categories",
                            text= "This page lets you customize how you receive notifications on specific Categories:\n"+
                                    " • Classes Notifications\n" +
                                    " • Exams Notifications\n" +
                                    " • Navigation Notifications"
                        )
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp, bottom = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF800000),
                    contentColor = Color.White
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            TitleTextAndIcon(url =R.drawable.map_routing_settings, description = "Map Routing Settings")
                        }
                        Text(
                            textAlign = TextAlign.Justify,
                            text = " • User Speed – Adjusts travel time estimates based on walking or cycling speed.\n"+
                                   " • Parking Radius – Finds the nearest parking space relative to your destination.\n"+
                                   " • Walking Distance – Determines the maximum walking distance before considering jeepney rides (min: 500m).\n"+
                                   " • Forestry Double Ride – Enables a two-jeepney route to and from UPLB Forestry."
                        )
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp, bottom = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF800000),
                    contentColor = Color.White
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        url = R. drawable.user_interface_settings
                        Text(
                            text = "Color Schemes & UI Customization",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                        IconButton(
                            onClick = { showPopup = true }
                        ) {
                            Image(
                                painter = painterResource(R.drawable.image_icon),
                                contentDescription = "Color Schemes & UI Customization",
                                modifier = Modifier.size(18.dp),
                                colorFilter = ColorFilter.tint(
                                    if(selectedThemeMode == ThemeMode.DARK)
                                        MaterialTheme.colorScheme.onSurface else Color.White
                                )
                            )
                        }
                        Text(
                            textAlign = TextAlign.Justify,
                            text = "• Users can modify or add colors for a more personalized experience.\n"+
                                   "• Users can also customize the application's user interface."
                        )
                    }
                }
            }

        }
    }
    if (showPopup) {
        PopupImageDialog(url = url, onClose = { showPopup = false })
    }
}