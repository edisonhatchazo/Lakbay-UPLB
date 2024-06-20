package com.example.classschedule.ui.exam

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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.classschedule.R
import com.example.classschedule.data.ExamSchedule
import com.example.classschedule.ui.navigation.AppViewModelProvider
import com.example.classschedule.ui.navigation.NavigationDestination
import com.example.classschedule.ui.screen.ScheduleScreenTopAppBar
import com.example.classschedule.ui.theme.ColorPalette

object ExamHomeDestination: NavigationDestination {
    override val route = "exam_home"
    override val titleRes = R.string.exam_schedule
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamHomeScreen(
    navigateToExamScheduleEntry: () -> Unit,
    navigateToExamScheduleUpdate: (Int) -> Unit,
    navigateToClassHomeDestination: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExamHomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.examHomeUiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ScheduleScreenTopAppBar(
                title = stringResource(R.string.exams),
                canNavigateBack = false,
                navigateToScheduleEntry = navigateToExamScheduleEntry,
                navigateToClassHome = navigateToClassHomeDestination,
                navigateToExamHome = {}
            )
        }
    ){ innerPadding ->
        Homebody(
            examScheduleList = homeUiState.examScheduleList,
            onExamScheduleClick = navigateToExamScheduleUpdate,
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding,
        )
    }
}

@Composable
private fun Homebody(
    examScheduleList: List<ExamSchedule>,
    onExamScheduleClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ){
        if(examScheduleList.isEmpty()){
            Text(
                text = stringResource(R.string.no_exams_description),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(contentPadding)
            )
        }else{
            ExamList(
                examScheduleList = examScheduleList,
                onExamScheduleClick = {onExamScheduleClick(it.id)},
                contentPadding = contentPadding,
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))
            )
        }
    }
}

@Composable
private fun ExamList(
    examScheduleList: List<ExamSchedule>,
    onExamScheduleClick: (ExamSchedule) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
){
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(items = examScheduleList, key = {it.id}) { item ->
            ExamDetails(
                examSchedule = item,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
                    .clickable { onExamScheduleClick(item) })
        }
    }
}

@Composable
private fun ExamDetails(
    examSchedule: ExamSchedule,
    modifier: Modifier = Modifier
){
    val colorEntry = ColorPalette.getColorEntry(examSchedule.colorName)
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
                    text = examSchedule.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = colorEntry.fontColor
                )
            }
        }
    }
}