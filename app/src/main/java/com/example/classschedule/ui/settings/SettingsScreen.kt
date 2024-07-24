package com.example.classschedule.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.classschedule.R
import com.example.classschedule.ui.navigation.NavigationDestination

object SettingsDestination: NavigationDestination {
    override val route = "settings"
    override val titleRes = R.string.settings
}

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    navigateToColorEntry: () -> Unit,
    navigateToCollegeColors: () -> Unit,
    navigateToRoutesColors: () -> Unit,
    navigateToWalkingSpeed: () -> Unit,
    navigateToCyclingSpeed: () -> Unit,
    openDrawer: () -> Unit,
) {
    Scaffold(
        topBar = {
            SettingsScreenTopAppBar(
                title = stringResource(SettingsDestination.titleRes),
                openDrawer = openDrawer
            )
        }
    ) { innerPadding ->
        MainSettingsScreen(
            navigateToColorEntry = navigateToColorEntry,
            navigateToCollegeColors = navigateToCollegeColors,
            navigateToRoutesColors = navigateToRoutesColors,
            navigateToWalkingSpeed = navigateToWalkingSpeed,
            navigateToCyclingSpeed = navigateToCyclingSpeed,
            modifier = modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding()
                )
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        )
    }
}

@Composable
fun MainSettingsScreen(
    navigateToColorEntry: () -> Unit,
    navigateToCollegeColors: () -> Unit,
    navigateToRoutesColors: () -> Unit,
    navigateToWalkingSpeed: () -> Unit,
    navigateToCyclingSpeed: () -> Unit,
    modifier: Modifier,
) {
    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        // App Theme
        SettingCategory(
            header = "App Theme",
            items = listOf(
                SettingItem(
                    title = "Dark Mode",
                    onClick = { /* Handle Dark Mode Toggle */ },
                    icon = Icons.Default.PlayArrow // Example icon, replace with actual toggle logic
                )
            )
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium)))
        // Color Schemes
        SettingCategory(
            header = "Color Schemes",
            items = listOf(
                SettingItem(
                    title = "Color Schemes",
                    onClick = navigateToColorEntry,
                    icon = Icons.Default.Edit
                ),
                SettingItem(
                    title = "UPLB Colleges and Units",
                    onClick = navigateToCollegeColors,
                    icon = Icons.Default.Edit
                ),
                SettingItem(
                    title = "Routes",
                    onClick = navigateToRoutesColors,
                    icon = Icons.Default.Edit
                )
            )
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium)))
        // Map Routing
        SettingCategory(
            header = "Map Routing",
            items = listOf(
                SettingItem(
                    title = "Walking Speed",
                    onClick = navigateToWalkingSpeed,
                    icon = Icons.Default.Edit
                ),
                SettingItem(
                    title = "Cycling Speed",
                    onClick = navigateToCyclingSpeed,
                    icon = Icons.Default.Edit
                )
            )
        )
    }
}

@Composable
fun SettingCategory(
    header: String,
    items: List<SettingItem>,
) {
    Text(
        text = header,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.padding_small))
    )
    items.forEach { item ->
        SettingItemRow(item = item)
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))
    }
}

@Composable
fun SettingItemRow(item: SettingItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = item.onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_medium)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = item.title)
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = item.onClick) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = Color.White
                )
            }
        }
    }
}

data class SettingItem(
    val title: String,
    val onClick: () -> Unit,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenTopAppBar(
    title: String,
    openDrawer: () -> Unit
){
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Blue),
        title = { Text( title,color = Color.White) },
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(onClick = openDrawer) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = stringResource(R.string.menu),
                    tint = Color.White
                )
            }
        }
    )
}
