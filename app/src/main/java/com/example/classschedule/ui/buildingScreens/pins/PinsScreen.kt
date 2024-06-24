package com.example.classschedule.ui.buildingScreens.pins

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.classschedule.R
import com.example.classschedule.data.Pins
import com.example.classschedule.ui.navigation.AppViewModelProvider
import com.example.classschedule.ui.navigation.NavigationDestination
import com.example.classschedule.ui.screen.PinsScreenTopAppBar

object PinsHomeDestination: NavigationDestination {
    override val route = "pins_home"
    override val titleRes = R.string.my_pins
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinsScreen(
    navigateToPinsEntry: () -> Unit,
    navigateToPinsUpdate: (Int) -> Unit,
    navigateToBuildingHomeDestination: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PinsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.pinsHomeUiState.collectAsState()
    Scaffold(
        modifier = modifier,
        topBar = {
            PinsScreenTopAppBar(
                title = stringResource(R.string.my_pins),
                canNavigateBack = false,
                navigateToPinEntry = navigateToPinsEntry,
                navigateToBuildingHome = navigateToBuildingHomeDestination
            )
        }
    ){ innerPadding ->
        PinsHomeBody(
            pinsList = homeUiState.pinsList,
            onPinsClick = navigateToPinsUpdate,
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding,
        )
    }
}

@Composable
private fun PinsHomeBody(
    pinsList: List<Pins>,
    onPinsClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ){
        if(pinsList.isEmpty()){
            Text(
                text = stringResource(R.string.no_pins_description),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(contentPadding)
            )
        }else{
            PinsList(
                pinsList = pinsList,
                onPinsClick = {onPinsClick(it.id)},
                contentPadding = contentPadding,
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))
            )
        }
    }
}

@Composable
private fun PinsList(
    pinsList: List<Pins>,
    onPinsClick: (Pins) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
){
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(items = pinsList, key = {it.id}) { item ->
            PinsDetails(
                pin = item,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
                    .clickable { onPinsClick(item) })
        }
    }
}

@Composable
private fun PinsDetails(
    pin: Pins,
    modifier: Modifier = Modifier
){
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ){
            Row(
                modifier = Modifier.fillMaxWidth()
            ){
                Text(
                    text = pin.title,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}
