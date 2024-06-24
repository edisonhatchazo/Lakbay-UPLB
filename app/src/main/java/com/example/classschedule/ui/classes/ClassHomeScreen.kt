package com.example.classschedule.ui.classes

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
import com.example.classschedule.data.ClassSchedule
import com.example.classschedule.ui.navigation.AppViewModelProvider
import com.example.classschedule.ui.navigation.NavigationDestination
import com.example.classschedule.ui.screen.ScheduleScreenTopAppBar
import com.example.classschedule.ui.theme.ColorPalette.getColorEntry

object ClassHomeDestination: NavigationDestination {
    override val route = "class_home"
    override val titleRes = R.string.app_name
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassHomeScreen(
    navigateToClassScheduleEntry: () -> Unit,
    navigateToClassScheduleUpdate: (Int) -> Unit,
    navigateToExamHomeDestination: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ClassHomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.classHomeUiState.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            ScheduleScreenTopAppBar(
                title = stringResource(R.string.classes),
                canNavigateBack = false,
                navigateToScheduleEntry = navigateToClassScheduleEntry,
                navigateToClassHome = {},
                navigateToExamHome = navigateToExamHomeDestination
            )
        }
    ){ innerPadding ->
        HomeBody(
            classScheduleList = homeUiState.classScheduleList,
            onClassScheduleClick = navigateToClassScheduleUpdate,
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding,
        )
    }
}

@Composable
private fun HomeBody(
    classScheduleList: List<ClassSchedule>,
    onClassScheduleClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ){
        if(classScheduleList.isEmpty()){
            Text(
                text = stringResource(R.string.no_class_description),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(contentPadding)
            )
        }else{
            ClassList(
                classScheduleList = classScheduleList,
                onClassScheduleClick = {onClassScheduleClick(it.id)},
                contentPadding = contentPadding,
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))
            )
        }
    }
}

@Composable
private fun ClassList(
    classScheduleList: List<ClassSchedule>,
    onClassScheduleClick: (ClassSchedule) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
){
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(items = classScheduleList, key = {it.id}) { item ->
            ClassDetails(
                classSchedule = item,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
                    .clickable { onClassScheduleClick(item) })
        }
    }
}

@Composable
private fun ClassDetails(
    classSchedule: ClassSchedule,
    modifier: Modifier = Modifier
){
    val colorEntry = getColorEntry(classSchedule.colorName)
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = colorEntry.backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ){
            Row(
                modifier = Modifier.fillMaxWidth()
            ){
                Text(
                    text = classSchedule.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = colorEntry.fontColor
                )
            }
        }
    }
}