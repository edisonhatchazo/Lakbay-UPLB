package com.edison.lakbayuplb.ui.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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


object AboutExamsDestination: NavigationDestination {
    override val route = "about_exams"
    override val titleRes = R.string.exams
}

@Composable
fun AboutExams(
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
                title = stringResource(R.string.exams),
                canNavigateBack = true,
                navigateUp = navigateBack,
                topAppBarBackgroundColor = topAppBarBackgroundColor,
                topAppBarForegroundColor = topAppBarForegroundColor,
            )
        }
    ){ innerPadding ->
        ExamsBody(innerPadding)
    }
}

@Composable
fun ExamsBody(
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
                                url = R.drawable.exam_schedule,
                                description = "Exam Schedule"
                            )
                            TextAndIcon(
                                url = R.drawable.exam_list,
                                description = "and Exams List"
                            )
                        }
                        Text(
                            textAlign = TextAlign.Center,
                            text = "This screen lets you track upcoming exams by date and subject. The layout ensures you never miss an exam."
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
                            text = "Adding or Modifying Exams",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                        ClassesDescription(
                            url = R.drawable.exam_location_selection,
                            description = "Choosing Location",
                            text= "When typing a location, the system auto-suggests rooms from the database for accuracy."
                        )
                        ClassesDescription(
                            url = R.drawable.exam_date_selection,
                            description = "Choosing Date",
                            text= "Select the exam date from the calendar."
                        )
                        ClassesDescription(
                            url = R.drawable.exam_time_selection,
                            description = "Setting Schedule",
                            text= "Pick start and end times for the exam."
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
                                url = R.drawable.exam_schedule_details,
                                description = "Exam Schedule Details"
                            )
                        }
                        Text(
                            textAlign = TextAlign.Center,
                            text = "The Exam Details Page provides a complete overview of a scheduled exam, ensuring students can easily track important exam information."
                        )
                        ClassesDetailsDescription(title = "Guide to Location Button", description = "Tap this button to quickly navigate to the exam venue using the app’s built-in map and route guidance.")

                    }
                }
            }
        }
    }
}
