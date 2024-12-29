package com.edison.lakbayuplb.ui.buildingScreens.uplb.rooms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edison.lakbayuplb.R
import com.edison.lakbayuplb.ui.navigation.AppViewModelProvider
import com.edison.lakbayuplb.ui.navigation.NavigationDestination
import com.edison.lakbayuplb.ui.screen.EntryScreenTopAppBar
import com.edison.lakbayuplb.ui.settings.global.TopAppBarColorSchemesViewModel
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
    navigateToAboutPage: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RoomEditViewModel = viewModel(factory = AppViewModelProvider.Factory),
    colorViewModel: TopAppBarColorSchemesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val topAppBarColors = colorViewModel.topAppBarColors.collectAsState()
    val (topAppBarBackgroundColor, topAppBarForegroundColor) = topAppBarColors.value

    val coroutineScope = rememberCoroutineScope()
    val classroomUiState = viewModel.classroomUiState
    Scaffold(
        topBar = {
            EntryScreenTopAppBar(
                title = stringResource(ClassroomEntryDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onNavigateUp,
                navigateToAboutPage = navigateToAboutPage,
                topAppBarForegroundColor = topAppBarForegroundColor,
                topAppBarBackgroundColor = topAppBarBackgroundColor
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
