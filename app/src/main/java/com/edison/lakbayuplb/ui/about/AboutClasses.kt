package com.edison.lakbayuplb.ui.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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


object AboutClassesDestination: NavigationDestination {
    override val route = "about_classes"
    override val titleRes = R.string.about_classes
}

@Composable
fun AboutClasses(
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
                title = stringResource(R.string.about_classes),
                canNavigateBack = true,
                navigateUp = navigateBack,
                topAppBarBackgroundColor = topAppBarBackgroundColor,
                topAppBarForegroundColor = topAppBarForegroundColor,
            )
        }
    ){ innerPadding ->
        ClassesBody(innerPadding)
    }
}

@Composable
fun ClassesBody(
    innerPadding: PaddingValues = PaddingValues(16.dp),
){
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
                    .padding(top = 16.dp, bottom = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF800000),
                    contentColor = Color.White
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ){
                            TextAndIcon(
                                url = R.drawable.class_schedule,
                                description = "Class Schedule"
                            )
                            TextAndIcon(
                                url = R.drawable.class_list,
                                description = "and Class List"
                            )
                        }
                        Text(
                            textAlign = TextAlign.Center,
                            text = "This screen helps you manage your class schedules by listing all your upcoming classes. You can view your daily schedule at a glance and organize your academic routine."
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
                        Text(
                            text = "Adding or Modifying Classes",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                        ClassesDescription(
                            url = R.drawable.class_location_selection,
                            description = "Choosing Location",
                            text= "When typing a location, the system auto-suggests rooms from the database for accuracy."
                        )
                        ClassesDescription(
                            url = R.drawable.class_days_selection,
                            description = "Choosing Days",
                            text= "Select up to two days per class session."
                        )
                        ClassesDescription(
                            url = R.drawable.class_time_selection,
                            description = "Setting Schedule",
                            text= "Pick start and end times for the class."
                        )
                        ClassesDescription(
                            url = R.drawable.color_selection,
                            description = "Color Coding",
                            text= "Assign a color to the class to distinguish it on the schedule screen."
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
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ){
                            TextAndIcon(
                                url = R.drawable.class_schedule_details,
                                description = "Class Schedule Details"
                            )
                        }
                        Text(
                            textAlign = TextAlign.Center,
                            text = "The Class Details Page provides a complete overview of a selected class, helping students quickly access important information about their schedule."
                        )
                        ClassesDetailsDescription(title = "Guide to Location Button", description = "Tap this button to instantly navigate to your classroom or building using the app’s built-in map and route guidance.")

                    }
                }
            }
        }
    }
}
