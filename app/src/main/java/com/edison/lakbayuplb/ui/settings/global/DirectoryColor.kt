package com.edison.lakbayuplb.ui.settings.global

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edison.lakbayuplb.R
import com.edison.lakbayuplb.ui.navigation.AppViewModelProvider
import com.edison.lakbayuplb.ui.navigation.NavigationDestination
import com.edison.lakbayuplb.ui.screen.DirectoryTopAppBar
import kotlinx.coroutines.launch

object DirectoryColorsDestination: NavigationDestination {
    override val route = "directory_colors"
    override val titleRes = R.string.directory
    const val COLLEGE_ARG = "college"
    const val COLORID_ARG = "colorId"
    val routeWithArgs = "$route/{$COLLEGE_ARG}/{$COLORID_ARG}"
}


@Composable
fun ColorDirectory(
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: DirectoryColorViewModel = viewModel(factory = AppViewModelProvider.Factory),
    colorViewModel: TopAppBarColorSchemesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val topAppBarColors = colorViewModel.topAppBarColors.collectAsState()
    val (topAppBarBackgroundColor, topAppBarForegroundColor) = topAppBarColors.value

    val collegeFullName = viewModel.college
    val previousColorId = viewModel.previousColorId
    val fullNameToAbbreviation = mapOf(
        "College of Arts and Sciences" to "CAS",
        "College of Development Communication" to "CDC",
        "College of Agriculture" to "CA",
        "College of Veterinary Medicine" to "CVM",
        "College of Human Ecology" to "CHE",
        "College of Public Affairs and Development" to "CPAf",
        "College of Forestry and Natural Resources" to "CFNR",
        "College of Economics and Management" to "CEM",
        "College of Engineering and Agro-industrial Technology" to "CEAT",
        "Graduate School" to "GS",
        "UP Unit" to "UP Unit",
        "Dormitory" to "Dormitory",
        "Landmark" to "Landmark"
    )
    val collegeAbbreviation = fullNameToAbbreviation[collegeFullName] ?: collegeFullName
    Scaffold(
        topBar = {
            DirectoryTopAppBar(
                title =  "Select Color for $collegeAbbreviation" ,
                canNavigateBack = canNavigateBack,
                onNavigateUp = onNavigateUp,
                topAppBarBackgroundColor = topAppBarBackgroundColor,
                topAppBarForegroundColor = topAppBarForegroundColor
            )
        }
    ) { innerPadding ->
        DirectoryColorScreen(
            viewModel = viewModel,
            previousColorId = previousColorId,
            navigateBack = onNavigateUp,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
    }
}

@Composable
fun DirectoryColorScreen(
    previousColorId: Int,
    navigateBack: () -> Unit,
    viewModel: DirectoryColorViewModel,
    modifier: Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val colors by viewModel.existingColors.collectAsState()

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(8.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(colors) { colorScheme ->
            Box(
                modifier = Modifier
                    .height(60.dp)
                    .background(color = Color(colorScheme.backgroundColor))
                    .clickable {
                        coroutineScope.launch { viewModel.updateColor(previousColorId, colorScheme.id)}
                        viewModel.updateCollegeColor(viewModel.college, colorScheme.id)
                        navigateBack()
                    }
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    colorScheme.name,
                    color = Color(colorScheme.fontColor),
                    fontSize = 14.sp
                )
            }
        }
    }
}


