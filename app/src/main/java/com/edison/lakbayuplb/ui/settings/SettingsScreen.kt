package com.edison.lakbayuplb.ui.settings

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
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edison.lakbayuplb.R
import com.edison.lakbayuplb.ui.navigation.AppViewModelProvider
import com.edison.lakbayuplb.ui.navigation.NavigationDestination
import com.edison.lakbayuplb.ui.screen.SettingsScreenTopAppBar
import com.edison.lakbayuplb.ui.settings.global.RouteSettingsViewModel
import com.edison.lakbayuplb.ui.settings.global.TopAppBarColorSchemesViewModel
import com.edison.lakbayuplb.ui.theme.ThemeMode

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
    navigateToRoutingSettings: () -> Unit,
    navigateToTopAppBarColors: () -> Unit,
    openDrawer: () -> Unit,
    onThemeChange: (ThemeMode) -> Unit,
    colorViewModel: TopAppBarColorSchemesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val topAppBarColors = colorViewModel.topAppBarColors.collectAsState()
    val (topAppBarBackgroundColor, topAppBarForegroundColor) = topAppBarColors.value

    Scaffold(
        topBar = {
            SettingsScreenTopAppBar(
                title = stringResource(SettingsDestination.titleRes),
                openDrawer = openDrawer,
                topAppBarBackgroundColor = topAppBarBackgroundColor,
                topAppBarForegroundColor = topAppBarForegroundColor
            )
        }
    ) { innerPadding ->
        MainSettingsScreen(
            navigateToColorEntry = navigateToColorEntry,
            navigateToCollegeColors = navigateToCollegeColors,
            navigateToRoutesColors = navigateToRoutesColors,
            navigateToRoutingSettings = navigateToRoutingSettings,
            navigateToTopAppBarColors = navigateToTopAppBarColors,
            onThemeChange = onThemeChange,
            modifier = modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                )
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        )
    }
}

@Composable
fun MainSettingsScreen(
    onThemeChange: (ThemeMode) -> Unit,
    navigateToColorEntry: () -> Unit,
    navigateToCollegeColors: () -> Unit,
    navigateToRoutesColors: () -> Unit,
    navigateToTopAppBarColors: () -> Unit,
    navigateToRoutingSettings: () -> Unit,
    routeSettingsViewModel: RouteSettingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier,
) {
    var selectedThemeMode by remember { mutableStateOf(ThemeMode.SYSTEM) }
    val isClassesNotificationEnabled by routeSettingsViewModel.classNotificationEnabled.collectAsState()
    val isExamsNotificationEnabled by routeSettingsViewModel.examNotificationEnabled.collectAsState()
    val context = LocalContext.current
    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        // App Theme
        Text(text = "App Theme", style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { selectedThemeMode = ThemeMode.DARK }
                .padding(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selectedThemeMode == ThemeMode.DARK,
                onClick = { selectedThemeMode = ThemeMode.DARK }
            )
            Text(text = "Dark Mode")
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { selectedThemeMode = ThemeMode.LIGHT }
                .padding(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selectedThemeMode == ThemeMode.LIGHT,
                onClick = { selectedThemeMode = ThemeMode.LIGHT }
            )
            Text(text = "Light Mode")
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { selectedThemeMode = ThemeMode.SYSTEM }
                .padding(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selectedThemeMode == ThemeMode.SYSTEM,
                onClick = { selectedThemeMode = ThemeMode.SYSTEM }
            )
            Text(text = "Based on System Settings")
        }

        Spacer(modifier = Modifier.height(4.dp))

        Button(onClick = { onThemeChange(selectedThemeMode) }) {
            Text(text = "Apply Theme")
        }


        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium)))
        // Color Schemes
        SettingCategory(
            header = stringResource(R.string.color_scheme),
            items = listOf(
                SettingItem(
                    title = stringResource(R.string.color_scheme),
                    onClick = navigateToColorEntry,
                    icon = Icons.Default.Edit
                ),
                SettingItem(
                    title = stringResource(R.string.uplb_college_units),
                    onClick = navigateToCollegeColors,
                    icon = Icons.Default.Edit
                ),
                SettingItem(
                    title = stringResource(R.string.top_app_bar),
                    onClick = navigateToTopAppBarColors,
                    icon = Icons.Default.Edit
                )
            )
        )
        // Map Routing
        SettingCategory(
            header = stringResource(R.string.map_routing),
            items = listOf(
                SettingItem(
                    title = stringResource(R.string.map_routing_settings),
                    onClick = navigateToRoutingSettings,
                    icon = Icons.Default.Edit
                ),
            )
        )

        NotificationCategory(
            header = stringResource(R.string.notifications),
            items = listOf(
                NotificationItem(
                    title = stringResource(R.string.classes),
                    initialToggleState = isClassesNotificationEnabled,
                    onToggleChanged = {isEnabled ->
                        routeSettingsViewModel.toggleClassNotificationEnabled(context,isEnabled)
                    }
                ),
                NotificationItem(
                    title = stringResource(R.string.exams),
                    initialToggleState = isExamsNotificationEnabled,
                    onToggleChanged = {isEnabled ->
                        routeSettingsViewModel.toggleExamNotificationEnabled(context,isEnabled)
                    }
                )
            )
        )
    }
}

@Composable
fun NotificationCategory(
    header: String,
    items: List<NotificationItem>
){
    Text(
        text = header,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.padding_small))
    )
    items.forEach{item ->
        NotificationItemToggle(item = item)
    }
}


@Composable
fun NotificationItemToggle(item: NotificationItem){
    var isToggled by remember { mutableStateOf(item.initialToggleState) }
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "    ${item.title}", color = colors.onSurface)
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = isToggled,
            onCheckedChange = {
                isToggled = it
                item.onToggleChanged(it)
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = colors.primary,
                uncheckedThumbColor = colors.onSurface
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
    }
}

@Composable
fun SettingItemRow(item: SettingItem) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        ) {
        Text(text = "    ${item.title}",color = colors.onSurface)
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = item.onClick) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = colors.onSurface
            )
        }
    }

}

data class SettingItem(
    val title: String,
    val onClick: () -> Unit,
    val icon: ImageVector
)

data class NotificationItem(
    val title: String,
    val initialToggleState: Boolean,
    val onToggleChanged: (Boolean) -> Unit
)