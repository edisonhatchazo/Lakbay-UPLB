package com.example.classschedule.ui.building.uplb

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.classschedule.R
import com.example.classschedule.ui.navigation.NavigationDestination
import com.example.classschedule.ui.screen.BuildingsScreenTopAppBar

object BuildingHomeDestination: NavigationDestination {
    override val route = "building_home"
    override val titleRes = R.string.building_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildingHomeScreen(
    modifier: Modifier = Modifier,
    navigateToPinsHomeDestination: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            BuildingsScreenTopAppBar(
                title = stringResource(BuildingHomeDestination.titleRes),
                canNavigateBack = false,
                navigateToPinsHome = navigateToPinsHomeDestination,
            )
        }
    ){innerPadding->
        BuildingHomeBody(
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding,
            navigateToPinsHome = navigateToPinsHomeDestination,
        )
    }
}

@Composable
fun BuildingHomeBody(
    modifier: Modifier = Modifier,
    navigateToPinsHome: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp)
){
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium)),
        modifier = modifier
            .padding(contentPadding)
            .padding(top = dimensionResource(id = R.dimen.padding_medium)) // Add top padding here
    ) {
        OutlinedButton(
            onClick = { /* To Do*/},
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
                .height(50.dp)
        ) {
            Text("UPLB Buildings")
        }

        OutlinedButton(
            onClick = { navigateToPinsHome()},
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
                .height(50.dp)
        ) {
            Text("My Own Pins")
        }
    }
}