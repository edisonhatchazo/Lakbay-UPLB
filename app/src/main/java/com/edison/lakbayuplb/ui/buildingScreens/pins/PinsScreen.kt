package com.edison.lakbayuplb.ui.buildingScreens.pins

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edison.lakbayuplb.R
import com.edison.lakbayuplb.data.building.Pins
import com.edison.lakbayuplb.ui.navigation.AppViewModelProvider
import com.edison.lakbayuplb.ui.navigation.NavigationDestination
import com.edison.lakbayuplb.ui.screen.PinsScreenTopAppBar
import com.edison.lakbayuplb.ui.settings.global.TopAppBarColorSchemesViewModel
import com.edison.lakbayuplb.ui.theme.ColorEntry

object PinsHomeDestination: NavigationDestination {
    override val route = "pins_home"
    override val titleRes = R.string.my_pins
}

@Composable
fun PinsScreen(
    navigateToPinsEntry: () -> Unit,
    navigateToPinsUpdate: (Int) -> Unit,
    openDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PinsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    colorViewModel: TopAppBarColorSchemesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val topAppBarColors = colorViewModel.topAppBarColors.collectAsState()
    val (topAppBarBackgroundColor, topAppBarForegroundColor) = topAppBarColors.value

    val homeUiState by viewModel.pinsHomeUiState.collectAsState()
    Scaffold(
        modifier = modifier,
        topBar = {
            PinsScreenTopAppBar(
                title = stringResource(R.string.my_pins),
                canNavigateBack = false,
                navigateToPinEntry = navigateToPinsEntry,
                openDrawer = openDrawer,
                topAppBarBackgroundColor = topAppBarBackgroundColor,
                topAppBarForegroundColor = topAppBarForegroundColor
            )
        }
    ){ innerPadding ->
        PinsHomeBody(
            pinsList = homeUiState.pinsList,
            onPinsClick = navigateToPinsUpdate,
            viewModel = viewModel,
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding,
        )
    }
}

@Composable
private fun PinsHomeBody(
    pinsList: List<Pins>,
    onPinsClick: (Int) -> Unit,
    viewModel: PinsViewModel,
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
                viewModel = viewModel,
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
    viewModel: PinsViewModel,
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
                viewModel = viewModel,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
                    .clickable { onPinsClick(item) })
        }
    }
}

@Composable
private fun PinsDetails(
    pin: Pins,
    modifier: Modifier = Modifier,
    viewModel: PinsViewModel

){

    val colorSchemes by viewModel.colorSchemes.collectAsState()

    val colorEntry = remember(pin.colorId) {
        colorSchemes[pin.colorId] ?: ColorEntry(Color.Transparent, Color.Black)
    }
    val fontColor = colorEntry.fontColor
    val backgroundColor = colorEntry.backgroundColor
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = fontColor)
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
