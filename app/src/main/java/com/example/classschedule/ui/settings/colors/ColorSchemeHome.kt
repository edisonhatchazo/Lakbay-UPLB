package com.example.classschedule.ui.settings.colors

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.classschedule.R
import com.example.classschedule.ui.navigation.AppViewModelProvider
import com.example.classschedule.ui.navigation.NavigationDestination
import com.example.classschedule.ui.theme.Typography

object ColorHomeDestination: NavigationDestination {
    override val route = "colors_home"
    override val titleRes = R.string.color_scheme
}

@Composable
fun ColorSchemeHome(
    onNavigateUp: () -> Unit,
    navigateToColorDetails: (Int) -> Unit,
    navigateToColorEntry: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: ColorSchemeHomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    Scaffold(
        topBar = {
            ColorScreenTopAppBar(
                title = stringResource(ColorHomeDestination.titleRes),
                navigateToColorEntry = navigateToColorEntry,
                canNavigateBack = canNavigateBack,
                onNavigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        MainColorScreen(
            viewModel = viewModel,
            navigateToColorDetails = navigateToColorDetails,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
    }
}

@Composable
fun MainColorScreen(
    modifier: Modifier,
    navigateToColorDetails: (Int) -> Unit,
    viewModel: ColorSchemeHomeViewModel
){
    val colors by viewModel.existingColors.collectAsState()

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text(
            text = stringResource(id = R.string.modify_color),
            style = Typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(colors) { colorScheme ->
                Box(
                    modifier = Modifier
                        .height(60.dp) // Ensure square items
                        .background(color = Color(colorScheme.backgroundColor))
                        .clickable {navigateToColorDetails(colorScheme.id)}
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
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorScreenTopAppBar(
    title: String,
    navigateToColorEntry: () -> Unit,
    canNavigateBack: Boolean,
    onNavigateUp: () -> Unit = {},
){
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Blue),
        title = { Text( title,color = Color.White) },
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = onNavigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button),
                        tint = Color.White
                    )
                }
            }
        },
        actions = {

            IconButton(onClick = navigateToColorEntry) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.color_entry),
                    tint = Color.Yellow
                )
            }
        }
    )
}
