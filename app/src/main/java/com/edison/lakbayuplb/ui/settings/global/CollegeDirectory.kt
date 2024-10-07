package com.edison.lakbayuplb.ui.settings.global

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edison.lakbayuplb.R
import com.edison.lakbayuplb.ui.navigation.AppViewModelProvider
import com.edison.lakbayuplb.ui.navigation.NavigationDestination
import com.edison.lakbayuplb.ui.screen.DirectoryTopAppBar
import com.edison.lakbayuplb.ui.theme.CollegeColorPalette
import com.edison.lakbayuplb.ui.theme.Typography

object DirectoryHomeDestination: NavigationDestination {
    override val route = "directory_home"
    override val titleRes = R.string.directory
}

@Composable
fun CollegeDirectory(
    onNavigateUp: () -> Unit,
    navigateToColorDetails: (String,Int) -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: CollegeDirectoryViewModel = viewModel(factory = AppViewModelProvider.Factory),
    colorViewModel: TopAppBarColorSchemesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val topAppBarColors = colorViewModel.topAppBarColors.collectAsState()
    val (topAppBarBackgroundColor, topAppBarForegroundColor) = topAppBarColors.value

    Scaffold(
        topBar = {
            DirectoryTopAppBar(
                title = stringResource(DirectoryHomeDestination.titleRes),
                canNavigateBack = canNavigateBack,
                onNavigateUp = onNavigateUp,
                topAppBarBackgroundColor = topAppBarBackgroundColor,
                topAppBarForegroundColor = topAppBarForegroundColor
            )
        }
    ) { innerPadding ->
        DirectoryScreen(
            viewModel = viewModel,
            navigateToColorDetails = navigateToColorDetails,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
    }
}

@Composable
fun DirectoryScreen(
    modifier: Modifier = Modifier,
    navigateToColorDetails: (String,Int) -> Unit,
    viewModel: CollegeDirectoryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val collegesWithColors by viewModel.collegesWithColors.collectAsState()

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text(
            text = stringResource(id = R.string.modify_directory),
            style = Typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(collegesWithColors.toList()) { (collegeFullName, pair) ->
                val (abbreviation, colorEntry) = pair
                val colorId = CollegeColorPalette.getPreviousColorId(collegeFullName)?:0
                Box(
                    modifier = Modifier
                        .height(60.dp) // Ensure square items
                        .background(color = colorEntry.backgroundColor)
                        .clickable {
                            navigateToColorDetails(collegeFullName,colorId)
                        }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = abbreviation,
                            color = colorEntry.fontColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}




