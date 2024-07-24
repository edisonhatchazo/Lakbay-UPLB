package com.example.classschedule.ui.buildingScreens.uplb

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.classschedule.R
import com.example.classschedule.data.Classroom
import com.example.classschedule.ui.map.OSMCustomMapType
import com.example.classschedule.ui.map.OSMMapping
import com.example.classschedule.ui.navigation.AppViewModelProvider
import com.example.classschedule.ui.navigation.NavigationDestination
import com.example.classschedule.ui.screen.CoordinateEntryScreenTopAppBar
import com.example.classschedule.ui.theme.CollegeColorPalette
import kotlinx.coroutines.launch

object RoomDetailsDestination : NavigationDestination {
    override val route = "room_details"
    override val titleRes = R.string.room_detail_title
    const val ROOMIDARG = "roomId"
    val routeWithArgs = "$route/{$ROOMIDARG}"
}

@Composable
fun RoomDetailsScreen(
    navigateBack: () -> Unit,
    navigateToMap: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RoomDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {

    val roomUiState = viewModel.uiState.collectAsState()
    val room = roomUiState.value.classroomDetails.toClassroom()


    var mapType by remember { mutableStateOf(OSMCustomMapType.STREET) }

    Scaffold(
        topBar = {
            CoordinateEntryScreenTopAppBar(
                title = room.abbreviation,
                canNavigateBack = true,
                navigateUp = navigateBack,
            )
        }, modifier = modifier
    ){innerPadding ->
        ClassroomDetailsBody(
            mapType = mapType,
            navigateToMap = navigateToMap,
            classroomDetailsUiState = roomUiState.value,
            viewModel = viewModel,
            buildingCollege = room.college,
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

@Composable
private fun ClassroomDetailsBody(
    classroomDetailsUiState: ClassroomDetailsUiState,
    mapType: OSMCustomMapType,
    buildingCollege:String,
    navigateToMap: (Int) -> Unit,
    viewModel: RoomDetailsViewModel,
    modifier: Modifier = Modifier
){
    val classroom = classroomDetailsUiState.classroomDetails.toClassroom()

    val colorEntry = rememberUpdatedState(CollegeColorPalette.getColorEntry(buildingCollege))
    val fontColor = colorEntry.value.fontColor
    val backgroundColor = colorEntry.value.backgroundColor
    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        ClassroomDetailed(
            fontColor = fontColor,
            backgroundColor = backgroundColor,
            classroom = classroom,
            viewModel = viewModel,
            navigateToMap = navigateToMap,
            modifier = Modifier.fillMaxWidth(),
            mapType = mapType
        )

    }
}

@Composable
fun ClassroomDetailed(
    classroom: Classroom,
    modifier: Modifier = Modifier,
    mapType: OSMCustomMapType,
    navigateToMap: (Int) -> Unit,
    viewModel: RoomDetailsViewModel,
    fontColor: Color,
    backgroundColor: Color
){
    val coroutineScope = rememberCoroutineScope()
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.padding_medium)
            )
        ) {
            ClassroomDetailsRow(
                labelResID = R.string.title,
                fontColor = fontColor,
                classroomDetail = classroom.title,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                )
            )
            ClassroomDetailsRow(
                labelResID = R.string.floor,
                fontColor = fontColor,
                classroomDetail = classroom.floor,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                )
            )
        }
    }
    Text(text = stringResource(R.string.location), fontWeight = FontWeight.Bold)
    Box(
        modifier = Modifier
            .height(300.dp)
            .fillMaxWidth()
    ) {

        OSMMapping(
            title = classroom.title,
            latitude = classroom.latitude,
            longitude = classroom.longitude,
            styleUrl = mapType.styleUrl
        )
    }

    Button(
        onClick = {
            coroutineScope.launch{ viewModel.addOrUpdateMapData(classroom.toClassroomDetails())}
            navigateToMap(0)
        },
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = stringResource(R.string.guide))
    }
}

@Composable
private fun ClassroomDetailsRow(
    @StringRes labelResID: Int,
    classroomDetail: String,
    fontColor: Color,
    modifier: Modifier = Modifier,
){
    Row(modifier = modifier) {
        Text(text = stringResource(labelResID), color = fontColor)
        Spacer(modifier = Modifier.weight(1f))
        Text(text = classroomDetail, fontWeight = FontWeight.Bold, color = fontColor)
    }
}

