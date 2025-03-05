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
import com.edison.lakbayuplb.ui.settings.global.TopAppBarColorSchemesViewModel

object ModifyBuildingsAndRoomsDestination: NavigationDestination {
    override val route = "modifying_buildings"
    override val titleRes = R.string.modify_buildings_and_rooms
}

@Composable
fun ModifyBuildingsAndRooms(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    colorViewModel: TopAppBarColorSchemesViewModel = viewModel(factory = AppViewModelProvider.Factory),
){
    val topAppBarColors = colorViewModel.topAppBarColors.collectAsState()
    val (topAppBarBackgroundColor, topAppBarForegroundColor) = topAppBarColors.value
    Scaffold(
        modifier = modifier,
        topBar = {
            AboutScreensTopAppBar(
                title = stringResource(R.string.modify_buildings_and_rooms),
                url = R.drawable.lakbay_uplb_icon,
                navigateBack = navigateBack,
                topAppBarBackgroundColor = topAppBarBackgroundColor,
                topAppBarForegroundColor = topAppBarForegroundColor,
                description = "Modify Buildings And Rooms"
            )
        }
    ){ innerPadding ->
        ModifyBuildingsAndRoomsBody(innerPadding)
    }
}

@Composable
fun ModifyBuildingsAndRoomsBody(
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
                            url = R.drawable.about_guide_map,
                            description = "About Guide Map & Navigation"
                        )

                        Text(
                            textAlign = TextAlign.Center,
                            text = "The Guide Map helps you navigate the UPLB campus efficiently. It shows your current location, the recommended route, and real-time updates for your chosen transport mode."
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            TextAndIcon(
                                url = R.drawable.guide_map_routing_types,
                                description = "Navigation Modes"
                            )
                        }
                        Text(
                            textAlign = TextAlign.Justify,
                            text = "• Walking – For on-foot directions.\n"+
                                    "• Cycling – For bike-friendly routes.\n"+
                                    "• Car – For drivers with available parking suggestions.\n"+
                                    "• Transit – For routes including jeepney rides."
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

                        TextAndIcon(
                            url = R.drawable.guide_map_parts,
                            description = "Turn-by-Turn Directions (Breakdown of the navigation screen layout)"
                        )

                        Text(
                            textAlign = TextAlign.Justify,
                            text = "• Left Panel: Image-based turn directions with distance.\n"+
                                    "• Middle Panel: Text-based directions.\n"+
                                    "• Right Panel: Total distance remaining for your current mode of transport."
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
                            text = "Guide Map Features",
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Text(
                            textAlign = TextAlign.Justify,
                            text = "• Live GPS Tracking: See your current location and real-time updates as you move.\n"+
                                    "• Text-to-Speech Instructions: Navigation instructions can be read aloud for a hands-free experience. You can disable this feature in Settings."
                        )
                    }
                }
            }
        }
    }
}
