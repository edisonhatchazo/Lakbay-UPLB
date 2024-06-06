package com.example.classschedule.ui.classes

import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.classschedule.ui.classes.ClassScheduleTopAppBar
import com.example.classschedule.R
import com.example.classschedule.ui.AppViewModelProvider
import com.example.classschedule.ui.navigation.NavigationDestination
import com.example.classschedule.ui.theme.ClassScheduleTheme
import kotlinx.coroutines.launch

object ClassScheduleEditDestination: NavigationDestination {
    override val route = "class_schedule_edit"
    override val titleRes = R.string.edit_item_title
    const val CLASSSCHEDULEIDARG = "classScheduleId"
    val routeWithArgs = "$route/{$CLASSSCHEDULEIDARG}"
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassScheduleEditScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ClassScheduleEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            ClassScheduleTopAppBar(
                title = stringResource(ClassScheduleEditDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        },
        modifier = modifier
    ) { innerPadding ->
        ClassScheduleEntryBody(
            classScheduleUiState = viewModel.classScheduleUiState,
            onClassScheduleValueChange = viewModel::updateUiState,
            onTimeChange = viewModel::updateTime,
            onTimeEndChange = viewModel::updateTimeEnd,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.updateClassSchedule()
                    navigateBack()
                }
            },
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding()
                )
                .verticalScroll(rememberScrollState())
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ClassScheduleEditScreenPreview(){
    ClassScheduleTheme {
        ClassScheduleEditScreen(
            navigateBack = { /*Do Nothing*/ }, onNavigateUp = { /*Do Nothing*/ })
    }
}