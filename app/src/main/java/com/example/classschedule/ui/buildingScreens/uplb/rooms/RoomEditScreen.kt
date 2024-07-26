package com.example.classschedule.ui.buildingScreens.uplb.rooms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.classschedule.R
import com.example.classschedule.ui.map.OSMCustomMapType
import com.example.classschedule.ui.navigation.AppViewModelProvider
import com.example.classschedule.ui.navigation.NavigationDestination
import com.example.classschedule.ui.screen.EntryScreenTopAppBar
import kotlinx.coroutines.launch

object ClassroomEditDestination: NavigationDestination {
    override val route = "classroom_edit"
    override val titleRes = R.string.room_edit_title
    const val CLASROOMIDARG = "roomId"
    val routeWithArgs = "$route/{$CLASROOMIDARG}"
}

@Composable
fun RoomEditScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RoomEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val classroomUiState = viewModel.classroomUiState
    var mapType by remember { mutableStateOf(OSMCustomMapType.STREET) }
    Scaffold(
        topBar = {
            EntryScreenTopAppBar(
                title = stringResource(ClassroomEntryDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                )
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        ) {
            ClassroomEntryBody(
                buildingId = classroomUiState.classroomDetails.buildingId,
                college = classroomUiState.classroomDetails.college,
                classroomUiState = classroomUiState,
                mapType = mapType,
                onSaveClick = {
                    coroutineScope.launch {
                        viewModel.updateClassroom()
                        navigateBack()
                    }
                },
                onClassroomValueChange = viewModel::updateUiState,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
