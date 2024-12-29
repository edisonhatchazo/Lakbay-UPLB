package com.edison.lakbayuplb.ui.about

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edison.lakbayuplb.R
import com.edison.lakbayuplb.ui.navigation.AppViewModelProvider
import com.edison.lakbayuplb.ui.navigation.NavigationDestination
import com.edison.lakbayuplb.ui.settings.global.TopAppBarColorSchemesViewModel

object AboutHomeDestination: NavigationDestination {
    override val route = "about_home"
    override val titleRes = R.string.about
}


@Composable
fun AboutHome(
    modifier: Modifier = Modifier,
    openDrawer: () -> Unit,
    navigateToLakbay: () -> Unit,
    navigateToBuildingsAndRooms: () -> Unit,
    navigateToClasses: () -> Unit,
    navigateToExams: () -> Unit,
    navigateToAboutGuideMap: () -> Unit,
    navigateToMyOwnPins: () -> Unit,
    navigateToSettingsAndCustomization: () -> Unit,
    colorViewModel: TopAppBarColorSchemesViewModel = viewModel(factory = AppViewModelProvider.Factory),

){
    val topAppBarColors = colorViewModel.topAppBarColors.collectAsState()
    val (topAppBarBackgroundColor, topAppBarForegroundColor) = topAppBarColors.value
    Scaffold(
        modifier = modifier,
        topBar = {
            AboutTopAppBar(
                title = stringResource(R.string.about),
                openDrawer = openDrawer,
                topAppBarBackgroundColor = topAppBarBackgroundColor,
                topAppBarForegroundColor = topAppBarForegroundColor
            )
        }
    ){ innerPadding ->
        AboutHomeBody(innerPadding,
            navigateToLakbay = navigateToLakbay,
            navigateToBuildingsAndRooms = navigateToBuildingsAndRooms,
            navigateToAboutGuideMap = navigateToAboutGuideMap,
            navigateToClasses = navigateToClasses,
            navigateToExams = navigateToExams,
            navigateToMyOwnPins = navigateToMyOwnPins,
            navigateToSettingsAndCustomization = navigateToSettingsAndCustomization,
            )
    }

}
@Composable
fun AboutHomeBody(
    innerPadding: PaddingValues = PaddingValues(0.dp),
    navigateToLakbay: () -> Unit,
    navigateToBuildingsAndRooms: () -> Unit,
    navigateToClasses: () -> Unit,
    navigateToExams: () -> Unit,
    navigateToAboutGuideMap: () -> Unit,
    navigateToMyOwnPins: () -> Unit,
    navigateToSettingsAndCustomization: () -> Unit,
) {
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT

    LazyVerticalGrid(
        columns = GridCells.Fixed(if(isPortrait) 2 else 3),
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item{
            AboutCard(text = "Lakbay UPLB", onClick = navigateToLakbay)
        }
        item{
            AboutCard(text = "Classes", onClick = navigateToClasses)
        }
        item{
            AboutCard(text = "Exams", onClick = navigateToExams)
        }
        item{
            AboutCard(text = "Guide Map", onClick = navigateToAboutGuideMap)
        }
        item{
            AboutCard(text = "Buildings & Rooms", onClick = navigateToBuildingsAndRooms)
        }
        item{
            AboutCard(text = "My Own Pins", onClick = navigateToMyOwnPins)
        }
        item{
            AboutCard(text = "Settings & Customization", onClick = navigateToSettingsAndCustomization)
        }
    }
}

@Composable
fun AboutCard(text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .size(80.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF800000)) // Dark red background
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = text, color = Color.White, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutTopAppBar(
    title: String,
    openDrawer: () -> Unit,
    topAppBarBackgroundColor: Color,
    topAppBarForegroundColor: Color
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = topAppBarBackgroundColor),
        title = { Text(title, color = topAppBarForegroundColor) },
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(onClick = openDrawer) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = stringResource(R.string.menu),
                    tint = topAppBarForegroundColor
                )
            }
        }
    )
}
