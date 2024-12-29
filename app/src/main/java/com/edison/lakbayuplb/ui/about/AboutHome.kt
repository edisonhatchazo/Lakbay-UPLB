package com.edison.lakbayuplb.ui.about

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
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
    openDrawer: () -> Unit,
    colorViewModel: TopAppBarColorSchemesViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier = Modifier,
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
        AboutHomeBody(innerPadding)
    }

}

@Composable
fun AboutHomeBody(
    innerPadding: PaddingValues = PaddingValues(0.dp)
){

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
