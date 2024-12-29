package com.edison.lakbayuplb.ui.settings

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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edison.lakbayuplb.R
import com.edison.lakbayuplb.ui.navigation.AppViewModelProvider
import com.edison.lakbayuplb.ui.navigation.NavigationDestination
import com.edison.lakbayuplb.ui.screen.SettingsScreenTopAppBar
import com.edison.lakbayuplb.ui.settings.global.RouteSettingsViewModel
import com.edison.lakbayuplb.ui.settings.global.TopAppBarColorSchemesViewModel

object SettingsDestination: NavigationDestination {
    override val route = "settings"
    override val titleRes = R.string.settings
}

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    navigateToUserInterface: () -> Unit,
    navigateToRoutingSettings: () -> Unit,
    openDrawer: () -> Unit,
    navigateToAboutSettings: () -> Unit,
    colorViewModel: TopAppBarColorSchemesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val topAppBarColors = colorViewModel.topAppBarColors.collectAsState()
    val (topAppBarBackgroundColor, topAppBarForegroundColor) = topAppBarColors.value

    Scaffold(
        topBar = {
            SettingsScreenTopAppBar(
                title = stringResource(SettingsDestination.titleRes),
                openDrawer = openDrawer,
                navigateToAboutSettings = navigateToAboutSettings,
                topAppBarBackgroundColor = topAppBarBackgroundColor,
                topAppBarForegroundColor = topAppBarForegroundColor
            )
        }
    ) { innerPadding ->
        MainSettingsScreen(
            navigateToRoutingSettings = navigateToRoutingSettings,
            navigateToUserInterface = navigateToUserInterface,
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
    navigateToRoutingSettings: () -> Unit,
    navigateToUserInterface: () -> Unit,
    routeSettingsViewModel: RouteSettingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier,
) {
    val isClassesNotificationEnabled by routeSettingsViewModel.classNotificationEnabled.collectAsState()
    val isExamsNotificationEnabled by routeSettingsViewModel.examNotificationEnabled.collectAsState()
    val isNavigationNotificationEnabled by routeSettingsViewModel.navigationNotificationEnabled.collectAsState()
    val isSpeechEnabled by routeSettingsViewModel.speechEnabled.collectAsState()
    val context = LocalContext.current
    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium))
    ) {

        // Map Routing
        SettingCategory(
            header = stringResource(R.string.user_interface),
            items = listOf(
                SettingItem(
                    title = stringResource(R.string.user_interface_settings),
                    onClick = navigateToUserInterface,
                    icon = Icons.Default.Edit
                ),
            )
        )

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium)))
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
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium)))
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
                ),
                NotificationItem(
                    title = stringResource(R.string.navigation),
                    initialToggleState = isNavigationNotificationEnabled,
                    onToggleChanged = {isEnabled ->
                        routeSettingsViewModel.toggleNavigationNotificationEnabled(isEnabled)
                    }
                ),
                NotificationItem(
                    title = stringResource(R.string.speech),
                    initialToggleState = isSpeechEnabled,
                    onToggleChanged = {isEnabled ->
                        routeSettingsViewModel.toggleNavigationSpeechEnabled(isEnabled)
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
        style = MaterialTheme.typography.headlineSmall,
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



data class NotificationItem(
    val title: String,
    val initialToggleState: Boolean,
    val onToggleChanged: (Boolean) -> Unit
)