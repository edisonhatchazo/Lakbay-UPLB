package com.edison.lakbayuplb.ui.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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


object BuildingsAndRoomsDestination: NavigationDestination {
    override val route = "buildings_and_rooms"
    override val titleRes = R.string.buildings_and_rooms
}

@Composable
fun BuildingsAndRooms(
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
                title = stringResource(R.string.buildings_and_rooms),
                canNavigateBack = true,
                navigateUp = navigateBack,
                topAppBarBackgroundColor = topAppBarBackgroundColor,
                topAppBarForegroundColor = topAppBarForegroundColor,
            )
        }
    ){ innerPadding ->
        BuildingsAndRoomsBody(innerPadding)
    }
}

@Composable
fun BuildingsAndRoomsBody(
    innerPadding: PaddingValues = PaddingValues(0.dp),
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
                    ) {
                        TitleTextAndIcon(
                            url = R.drawable.about_buildings,
                            description = "About Buildings & Rooms"
                        )

                        Text(
                            textAlign = TextAlign.Center,
                            text = "This screen lists UPLB buildings categorized by colleges, UP units, landmarks, and non-UP units. You can search for a building or its rooms and offices using the Search Button."
                        )

                        ClassesDescription(
                            url = R.drawable.about_building_details,
                            description = "Building Page",
                            text= "Each building has a dedicated page displaying its name, college/unit, and a list of rooms, offices, and institutes inside."
                        )
                        ClassesDescription(
                            url = R.drawable.about_room_details,
                            description = "Room Page",
                            text= "The Room Page provides details about a specific room, including its name, purpose, and floor location."
                        )
                    }
                }
            }
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
                    ) {
                        Text(
                            text = "About Adding & Editing Buildings/Rooms",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold
                        )

                        ClassesDescription(
                            url = R.drawable.about_adding_building,
                            description = "Building Add/Edit Page",
                            text= "Users can add/edit building details, assign them to a unit/college, and set their location on the map."
                        )
                        ClassesDescription(
                            url = R.drawable.about_adding_rooms,
                            description = "Room Add/Edit Page",
                            text= "Rooms can be added or modified with details such as name, purpose (classroom, office, institute), and floor number."
                        )
                    }
                }
            }
        }
    }
}