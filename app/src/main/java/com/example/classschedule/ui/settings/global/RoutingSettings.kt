package com.example.classschedule.ui.settings.global

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.classschedule.R
import com.example.classschedule.ui.navigation.AppViewModelProvider
import com.example.classschedule.ui.navigation.NavigationDestination

object RoutingDestination: NavigationDestination {
    override val route = "routing_home"
    override val titleRes = R.string.map_routing_settings
}

enum class SpeedType {
    WALKING, CYCLING, CAR, JEEPNEY
}

fun Double.roundToHundredths(): Double {
    return "%.2f".format(this).toDouble()
}

@Composable
fun RoutingSettings(
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: RouteViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    Scaffold(
        topBar = {
            DirectoryTopAppBar(
                title = stringResource(RoutingDestination.titleRes),
                canNavigateBack = canNavigateBack,
                onNavigateUp = onNavigateUp
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
    viewModel: RouteViewModel,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedSpeedType by remember { mutableStateOf<SpeedType?>(null) }

    val walkingSpeed = viewModel.walkingSpeed.collectAsState().value.roundToHundredths()
    val cyclingSpeed = (viewModel.cyclingSpeed.collectAsState().value * 3.6).roundToHundredths()
    val carSpeed = (viewModel.carSpeed.collectAsState().value * 3.6).roundToHundredths()
    val jeepneySpeed = (viewModel.jeepneySpeed.collectAsState().value * 3.6).roundToHundredths()

    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        Text("Speed:",style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        RoutingDetail(
            title = stringResource(R.string.walking_speed),
            speed = "$walkingSpeed m/s",
            onEditClick = {
                selectedSpeedType = SpeedType.WALKING
                showDialog = true
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        RoutingDetail(
            title = stringResource(R.string.cycling_speed),
            speed = "$cyclingSpeed km/hr",
            onEditClick = {
                selectedSpeedType = SpeedType.CYCLING
                showDialog = true
            }
        )
        Spacer(modifier = Modifier.height(16.dp))

        TransportDetail(
            title = stringResource(R.string.car_speed),
            speed = "$carSpeed km/hr")
        Spacer(modifier = Modifier.height(16.dp))
        TransportDetail(
            title = stringResource(R.string.jeepney_speed),
            speed = "$jeepneySpeed km/hr")
        Spacer(modifier = Modifier.height(16.dp))

        Text("Minimum Walking Distance to nearest Jeepney Stop:",style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        TransportDetail(
            title = "Minimum Walking Distance",
            speed = "500m")
        Spacer(modifier = Modifier.height(8.dp))
        Text("Forestry Routes Double Ride:",style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        TransportDetail(
            title = "Forestry Route Double Ride",
            speed = "Disabled")

    }

    if (showDialog) {
        val currentSpeed = when (selectedSpeedType) {
            SpeedType.WALKING -> walkingSpeed.toString()
            SpeedType.CYCLING -> cyclingSpeed.toString()
            SpeedType.CAR -> carSpeed.toString()
            SpeedType.JEEPNEY ->jeepneySpeed.toString()
            null -> ""
        }

        SpeedInputDialog(
            title = "Set Speed",
            currentValue = currentSpeed,
            onValueChange = { newValue ->
                when (selectedSpeedType) {
                    SpeedType.WALKING -> viewModel.setWalkingSpeed(newValue.toDouble())
                    SpeedType.CYCLING -> viewModel.setCyclingSpeed(newValue.toDouble() / 3.6) // Convert km/h to m/s
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
fun RoutingDetail(
    title: String,
    speed: String,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Blue,
            contentColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_medium)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = speed, color = Color.Yellow)
            Spacer(modifier = Modifier.weight(0.25f))
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun TransportDetail(
    title: String,
    speed: String,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Blue,
            contentColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_medium)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title)
            Spacer(modifier = Modifier.weight(0.25f))
            Text(text = speed, color = Color.Yellow)
            Spacer(modifier = Modifier.weight(0.25f))

        }
    }
}

@Composable
fun SpeedInputDialog(
    title: String,
    currentValue: String,
    onValueChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var value by remember { mutableStateOf(currentValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = {
            Column {
                TextField(
                    value = value,
                    onValueChange = { value = it },
                    label = { Text("Speed") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Decimal
                    )
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onValueChange(value)
                onConfirm()
            }) {
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
