package com.edison.lakbayuplb.ui.exam

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
import com.edison.lakbayuplb.data.classes.ExamSchedule
import com.edison.lakbayuplb.ui.navigation.AppViewModelProvider
import com.edison.lakbayuplb.ui.navigation.NavigationDestination
import com.edison.lakbayuplb.ui.screen.ScheduleScreenTopAppBar
import com.edison.lakbayuplb.ui.settings.global.TopAppBarColorSchemesViewModel
import com.edison.lakbayuplb.ui.theme.ColorEntry

object ExamHomeDestination: NavigationDestination {
    override val route = "exam_home"
    override val titleRes = R.string.exam_schedule
}

@Composable
fun ExamHomeScreen(
    navigateToExamScheduleEntry: () -> Unit,
    navigateToExamScheduleUpdate: (Int) -> Unit,
    modifier: Modifier = Modifier,
    openDrawer: () -> Unit,
    viewModel: ExamHomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    colorViewModel: TopAppBarColorSchemesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val topAppBarColors = colorViewModel.topAppBarColors.collectAsState()
    val (topAppBarBackgroundColor, topAppBarForegroundColor) = topAppBarColors.value

    val homeUiState by viewModel.examHomeUiState.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            ScheduleScreenTopAppBar(
                title = stringResource(R.string.exams),
                canNavigateBack = false,
                navigateToScheduleEntry = navigateToExamScheduleEntry,
                openDrawer = openDrawer,
                topAppBarBackgroundColor = topAppBarBackgroundColor,
                topAppBarForegroundColor = topAppBarForegroundColor
            )
        }
    ){ innerPadding ->
        Homebody(
            examScheduleList = homeUiState.examScheduleList,
            onExamScheduleClick = navigateToExamScheduleUpdate,
            modifier = modifier.fillMaxSize(),
            viewModel = viewModel,
            contentPadding = innerPadding,
        )
    }
}

@Composable
private fun Homebody(
    examScheduleList: List<ExamSchedule>,
    onExamScheduleClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExamHomeViewModel,
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
                viewModel = viewModel,
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
    viewModel: ExamHomeViewModel,
    modifier: Modifier = Modifier
){
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(items = examScheduleList, key = {it.id}) { item ->
            ExamDetails(
                viewModel = viewModel,
                examSchedule = item,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
                    .clickable { onExamScheduleClick(item) })
        }
    }
}

@Composable
private fun ExamDetails(
    viewModel: ExamHomeViewModel,
    examSchedule: ExamSchedule,
    modifier: Modifier = Modifier
){
    val colorSchemes by viewModel.colorSchemes.collectAsState()

    val colorEntry = remember(examSchedule.colorId) {
        colorSchemes[examSchedule.colorId] ?: ColorEntry(Color.Transparent, Color.Black)
    }
    val fontColor = colorEntry.fontColor
    val backgroundColor = colorEntry.backgroundColor
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ){
            Row(
                modifier = Modifier.fillMaxWidth()
            ){
                Text(
                    text = "${examSchedule.title} ${examSchedule.section}",
                    style = MaterialTheme.typography.titleLarge,
                    color = fontColor
                )
            }
        }
    }
}