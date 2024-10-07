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

object TopAppBarColorsDestination: NavigationDestination {
    override val route = "top_appbar_colors"
    override val titleRes = R.string.top_app_bar
}
@Composable
fun TopAppBarColorSchemes(
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: TopAppBarColorSchemesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val previousColorId = viewModel.previousColorId
    val topAppBarColors = viewModel.topAppBarColors.collectAsState()
    val (topAppBarBackgroundColor, topAppBarForegroundColor) = topAppBarColors.value

    Scaffold(
        topBar = {
            DirectoryTopAppBar(
                title =  "Select Color for the Top App Bar",
                canNavigateBack = canNavigateBack,
                onNavigateUp = onNavigateUp,
                topAppBarBackgroundColor = topAppBarBackgroundColor,
                topAppBarForegroundColor = topAppBarForegroundColor
            )
        }
    ) { innerPadding ->
        TopAppBarColorScreen(
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
fun TopAppBarColorScreen(
    previousColorId: Int,
    navigateBack: () -> Unit,
    viewModel: TopAppBarColorSchemesViewModel,
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
                    .background(color = Color(colorScheme.backgroundColor)) // Background color for preview
                    .clickable {
                        coroutineScope.launch {
                            viewModel.updateColor(previousColorId, colorScheme.id) // Update selected color
                        }
                        navigateBack()
                    }
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = colorScheme.name,
                    color = Color(colorScheme.fontColor), // Use the font color here
                    fontSize = 14.sp
                )
            }
        }
    }
}
