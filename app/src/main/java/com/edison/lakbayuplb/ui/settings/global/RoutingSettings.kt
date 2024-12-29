package com.edison.lakbayuplb.ui.settings.global

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edison.lakbayuplb.R
import com.edison.lakbayuplb.ui.navigation.AppViewModelProvider
import com.edison.lakbayuplb.ui.navigation.NavigationDestination
import com.edison.lakbayuplb.ui.screen.AboutNavigationTopAppBar

object RoutingDestination: NavigationDestination {
    override val route = "routing_home"
    override val titleRes = R.string.map_routing_settings
}

enum class SpeedType {
    WALKING, CYCLING, CAR, JEEPNEY, WALKING_DISTANCE, PARKING_RADIUS
}

fun Double.roundToHundredths(): Double {
    return "%.2f".format(this).toDouble()
}

@Composable
fun RoutingSettings(
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    navigateToAboutSettings: () -> Unit,
    viewModel: RouteSettingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    colorViewModel: TopAppBarColorSchemesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val topAppBarColors = colorViewModel.topAppBarColors.collectAsState()
    val (topAppBarBackgroundColor, topAppBarForegroundColor) = topAppBarColors.value

    Scaffold(
        topBar = {
            AboutNavigationTopAppBar(
                title = stringResource(RoutingDestination.titleRes),
                canNavigateBack = canNavigateBack,
                onNavigateUp = onNavigateUp,
                navigateToAboutSettings = navigateToAboutSettings,
                topAppBarBackgroundColor = topAppBarBackgroundColor,
                topAppBarForegroundColor = topAppBarForegroundColor
            )
        }
    ) { innerPadding ->
        RoutingScreen(
            viewModel = viewModel,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
    }
}

@Composable
fun RoutingScreen(
    viewModel: RouteSettingsViewModel,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedSpeedType by remember { mutableStateOf<SpeedType?>(null) }

    val walkingSpeed = viewModel.walkingSpeed.collectAsState().value.roundToHundredths()
    val cyclingSpeed = (viewModel.cyclingSpeed.collectAsState().value * 3.6).roundToHundredths()
    val carSpeed = (viewModel.carSpeed.collectAsState().value * 3.6).roundToHundredths()
    val jeepneySpeed = (viewModel.jeepneySpeed.collectAsState().value * 3.6).roundToHundredths()
    val walkingDistance = viewModel.walkingDistance.collectAsState().value
    val parkingRadius = viewModel.parkingRadius.collectAsState().value

    val toggle = viewModel.forestryRouteDoubleRideEnabled.collectAsState().value
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        item {
            Text("Speed:", style = MaterialTheme.typography.titleMedium)
        }
        item {
            RoutingDetail(
                title = stringResource(R.string.walking_speed),
                speed = "$walkingSpeed m/s",
                onEditClick = {
                    selectedSpeedType = SpeedType.WALKING
                    showDialog = true
                }
            )
        }
        item {
            RoutingDetail(
                title = stringResource(R.string.cycling_speed),
                speed = "$cyclingSpeed km/hr",
                onEditClick = {
                    selectedSpeedType = SpeedType.CYCLING
                    showDialog = true
                }
            )
        }
        item {
            TransportDetail(
                title = stringResource(R.string.car_speed),
                speed = "$carSpeed km/hr"
            )
        }
        item {
            TransportDetail(
                title = stringResource(R.string.jeepney_speed),
                speed = "$jeepneySpeed km/hr"
            )
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Maximum Radius for Finding Parking (Car/Bicycle):",
                style = MaterialTheme.typography.titleMedium
            )
            RoutingDetail(
                title = stringResource(R.string.parking_radius),
                speed = "$parkingRadius meters",
                onEditClick = {
                    selectedSpeedType = SpeedType.PARKING_RADIUS
                    showDialog = true
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "The maximum distance you are willing to walk before considering riding a jeepney:",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            RoutingDetail(
                title = stringResource(R.string.walking_distance),
                speed = "$walkingDistance meters",
                onEditClick = {
                    selectedSpeedType = SpeedType.WALKING_DISTANCE
                    showDialog = true
                }
            )
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Forestry Routes Double Ride:", style = MaterialTheme.typography.titleMedium)

            ForestryRouteToggle(
                title = stringResource(R.string.forestry_route),
                initialToggleState = toggle
            ) {
                viewModel.setForestryRouteDoubleRideEnabled(it)
            }
        }


    }

    if (showDialog) {
        val currentSpeed = when (selectedSpeedType) {
            SpeedType.WALKING -> walkingSpeed.toString()
            SpeedType.CYCLING -> cyclingSpeed.toString()
            SpeedType.CAR -> carSpeed.toString()
            SpeedType.JEEPNEY ->jeepneySpeed.toString()
            SpeedType.WALKING_DISTANCE -> walkingDistance.toString()
            SpeedType.PARKING_RADIUS -> parkingRadius.toString()
            null -> ""
        }

        SpeedInputDialog(
            title = if(selectedSpeedType == SpeedType.PARKING_RADIUS) "Set Radius" else if(selectedSpeedType == SpeedType.WALKING_DISTANCE) "Set Distance" else "Set Speed",
            currentValue = currentSpeed,
            label = if (selectedSpeedType == SpeedType.WALKING_DISTANCE) "Walking Distance (in meters)" else "Speed",
            onValueChange = { newValue ->
                when (selectedSpeedType) {
                    SpeedType.WALKING -> viewModel.setWalkingSpeed(newValue.toDouble())
                    SpeedType.CYCLING -> viewModel.setCyclingSpeed(newValue.toDouble() / 3.6) // Convert km/h to m/s
                    SpeedType.WALKING_DISTANCE -> viewModel.setMinimumWalkingDistance(newValue.toInt())
                    SpeedType.PARKING_RADIUS -> viewModel.setParkingRadius(newValue.toDouble())
                    SpeedType.CAR -> { /* Car speed is fixed and not changeable */ }
                    SpeedType.JEEPNEY -> {/*Jeepney speed is fixed and not changeable*/}

                    null -> { /* Do nothing */ }
                }
            },
            onDismiss = { showDialog = false },
            onConfirm = { showDialog = false }
        )
    }
}

@Composable
fun ForestryRouteToggle(
    title: String,
    initialToggleState: Boolean,
    onToggleChanged: (Boolean) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    var isToggled by remember { mutableStateOf(initialToggleState) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, color = colors.onSurface)
        Spacer(modifier = Modifier.weight(0.25f))
        Switch(
            checked = isToggled,
            onCheckedChange = {
                isToggled = it
                onToggleChanged(it)
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = colors.primary,
                uncheckedThumbColor = colors.onSurface
            )
        )

    }

}

@Composable
fun RoutingDetail(
    title: String,
    speed: String,
    onEditClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "    $title",color = colors.onSurface)
        Spacer(modifier = Modifier.weight(1f))
        Text(text = speed, color = colors.onSecondary)
        Spacer(modifier = Modifier.weight(0.25f))
        IconButton(onClick = onEditClick) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = null,
                tint = colors.onSurface
            )
        }
    }

}

@Composable
fun TransportDetail(
    title: String,
    speed: String,
) {
    val colors = MaterialTheme.colorScheme
    val configuration = LocalConfiguration.current

    // Determine the weight for the first spacer based on the screen orientation
    val spacerWeight = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        0.7f  // Use 0.7f for landscape mode
    } else {
        0.25f // Use 0.25f for portrait mode
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, color = colors.onSurface)
        Spacer(modifier = Modifier.weight(spacerWeight))  // Use dynamic weight based on orientation
        Text(text = speed, color = colors.onSecondary)
        Spacer(modifier = Modifier.weight(0.25f))
    }
}


@Composable
fun SpeedInputDialog(
    title: String,
    currentValue: String,
    label: String,
    onValueChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var value by remember { mutableStateOf(currentValue) }
    var isValid by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = {
            Column {
                TextField(
                    value = value,
                    onValueChange = { newValue ->
                        value = newValue
                        isValid = if (label == "Walking Distance (in meters)") {
                            newValue.toIntOrNull()?.let { it >= 500 } ?: false
                        } else {
                            newValue.toDoubleOrNull() != null
                        }
                    },
                    label = { Text(label) },
                    isError = !isValid,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = if (label == "Walking Distance (in meters)") KeyboardType.Number else KeyboardType.Decimal
                    )
                )
                if (!isValid && label == "Walking Distance (in meters)") {
                    Text(
                        text = "Value must be at least 500",
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
                        onValueChange(value)
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
