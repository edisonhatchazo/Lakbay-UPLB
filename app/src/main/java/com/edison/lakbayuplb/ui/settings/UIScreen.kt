package com.edison.lakbayuplb.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edison.lakbayuplb.R
import com.edison.lakbayuplb.ui.navigation.AppViewModelProvider
import com.edison.lakbayuplb.ui.navigation.NavigationDestination
import com.edison.lakbayuplb.ui.screen.AboutNavigationTopAppBar
import com.edison.lakbayuplb.ui.settings.global.AppPreferences
import com.edison.lakbayuplb.ui.settings.global.TopAppBarColorSchemesViewModel
import com.edison.lakbayuplb.ui.theme.ThemeMode


object UserInterfaceDestination: NavigationDestination {
    override val route = "UI_home"
    override val titleRes = R.string.user_interface
}
@Composable
fun UIScreen(
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    modifier: Modifier = Modifier,
    onThemeChange: (ThemeMode) -> Unit,
    navigateToColorEntry: () -> Unit,
    navigateToAboutSettings: () -> Unit,
    navigateToCollegeColors: () -> Unit,
    navigateToTopAppBarColors: () -> Unit,
    colorViewModel: TopAppBarColorSchemesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val topAppBarColors = colorViewModel.topAppBarColors.collectAsState()
    val (topAppBarBackgroundColor, topAppBarForegroundColor) = topAppBarColors.value

    Scaffold(
        topBar = {
            AboutNavigationTopAppBar(
                title = stringResource(UserInterfaceDestination.titleRes),
                canNavigateBack = canNavigateBack,
                onNavigateUp = onNavigateUp,
                navigateToAboutSettings = navigateToAboutSettings,
                topAppBarBackgroundColor = topAppBarBackgroundColor,
                topAppBarForegroundColor = topAppBarForegroundColor
            )
        }
    ) { innerPadding ->
        MainUIScreen(
            navigateToColorEntry = navigateToColorEntry,
            navigateToCollegeColors = navigateToCollegeColors,
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
fun MainUIScreen(
    onThemeChange: (ThemeMode) -> Unit,
    navigateToColorEntry: () -> Unit,
    navigateToCollegeColors: () -> Unit,
    navigateToTopAppBarColors: () -> Unit,
    modifier: Modifier,
){

    var selectedThemeMode by remember { mutableStateOf(ThemeMode.SYSTEM) }
    val context = LocalContext.current
    val appPreferences = AppPreferences(context)

    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        // App Theme
        Text(text = "App Theme", style = MaterialTheme.typography.headlineMedium)
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
        SettingCategory(
            header = stringResource(R.string.color_schemes),
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


        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium)))
        FontSettingCategory(
            header = stringResource(R.string.schedule_font_size),
            items = listOf(
                FontSettingItem(
                    title = stringResource(R.string.font_size),
                    onClick = { /* No need, handled in the category */ },
                    icon = Icons.Default.Edit
                )
            ),
            appPreferences = appPreferences
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
        style = MaterialTheme.typography.headlineSmall,
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



@Composable
fun FontSettingCategory(
    header: String,
    items: List<FontSettingItem>,
    appPreferences: AppPreferences
) {
    var showDialog by remember { mutableStateOf(false) }
    var currentFontSize by remember { mutableStateOf(appPreferences.getFontSize().toString()) }

    Column {
        Text(
            text = header,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.padding_small))
        )

        items.forEach { item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "    ${item.title}")
                IconButton(onClick = { showDialog = true }) {
                    Icon(imageVector = item.icon, contentDescription = null)
                }
            }
        }

        if (showDialog) {
            FontSizeInputDialog(
                title = "Set Font Size",
                currentFontSize = currentFontSize,
                label = "Font Size (3-15)",
                onFontSizeChange = { newSize -> currentFontSize = newSize },
                onDismiss = { showDialog = false },
                onConfirm = {
                    appPreferences.setFontSize(currentFontSize.toFloat())
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun FontSizeInputDialog(
    title: String,
    currentFontSize: String,
    label: String,
    onFontSizeChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var fontSize by remember { mutableStateOf(currentFontSize) }
    var isValid by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = {
            Column {
                TextField(
                    value = fontSize,
                    onValueChange = { newValue ->
                        fontSize = newValue
                        isValid = newValue.toFloatOrNull()?.let { it in 3f..15f } ?: false
                    },
                    label = { Text(label) },
                    isError = !isValid,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    )
                )
                if (!isValid) {
                    Text(
                        text = "Font size must be between 3 and 15",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (isValid) {
                        onFontSizeChange(fontSize)
                        onConfirm()
                    }
                },
                enabled = isValid
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


data class FontSettingItem(
    val title: String,
    val onClick: () -> Unit,
    val icon: ImageVector
)

data class SettingItem(
    val title: String,
    val onClick: () -> Unit,
    val icon: ImageVector
)
