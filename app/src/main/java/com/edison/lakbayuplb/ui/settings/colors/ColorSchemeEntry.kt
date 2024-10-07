package com.edison.lakbayuplb.ui.settings.colors
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edison.lakbayuplb.R
import com.edison.lakbayuplb.ui.navigation.AppViewModelProvider
import com.edison.lakbayuplb.ui.navigation.NavigationDestination
import com.edison.lakbayuplb.ui.screen.ColorScreenDetailTopAppBar
import com.edison.lakbayuplb.ui.settings.global.TopAppBarColorSchemesViewModel
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import kotlinx.coroutines.launch

object ColorSchemeEntryDestination: NavigationDestination {
    override val route = "colors_entry"
    override val titleRes = R.string.color_entry
}

@Composable
fun ColorSchemeEntry(
    onNavigateUp: () -> Unit,
    navigateBack: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: ColorSchemeEntryViewModel = viewModel(factory = AppViewModelProvider.Factory),
    colorViewModel: TopAppBarColorSchemesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val topAppBarColors = colorViewModel.topAppBarColors.collectAsState()
    val (topAppBarBackgroundColor, topAppBarForegroundColor) = topAppBarColors.value

    val entryColorSchemesUiState = viewModel.entryColorSchemeUiState
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            ColorScreenDetailTopAppBar(
                title = stringResource(ColorSchemeEntryDestination.titleRes),
                canNavigateBack = canNavigateBack,
                onNavigateUp = onNavigateUp,
                topAppBarBackgroundColor = topAppBarBackgroundColor,
                topAppBarForegroundColor = topAppBarForegroundColor
            )
        }
    ) { innerPadding ->
        ColorPickingScreen(
            colorSchemeDetails = entryColorSchemesUiState.colorSchemeDetails,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveColorScheme()
                    navigateBack()
                }
            },
            onColorValueChange = viewModel::updateUiState,
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                )
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        )
    }
}

@Composable
fun ColorPickingScreen(
    colorSchemeDetails: ColorSchemeDetails,
    onSaveClick: () -> Unit,
    onColorValueChange: (ColorSchemeDetails) -> Unit,
    modifier: Modifier,
    enabled: Boolean = true
){
    var fontColor = Color(colorSchemeDetails.fontColor)
    var backgroundColor = Color(colorSchemeDetails.backgroundColor)
    var showFontColorPicker by remember { mutableStateOf(false) }
    var showBackgroundColorPicker by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = colorSchemeDetails.name,
            onValueChange = { onColorValueChange(colorSchemeDetails.copy(name = it)) },
            label = { Text("Color Name") },
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { showFontColorPicker = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Select Font Color")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { showBackgroundColorPicker = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Select Background Color")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .background(backgroundColor)
                .fillMaxWidth()
                .height(100.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(colorSchemeDetails.name, color = fontColor)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick =  onSaveClick,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Save Color")
        }
    }

    if (showFontColorPicker) {
        ColorPickerDialogs(
            initialColor = fontColor,
            onColorSelected = { selectedColor ->
                fontColor = selectedColor
                onColorValueChange(colorSchemeDetails.copy(fontColor = selectedColor.toArgb()))
                showFontColorPicker = false
            },
            onDismissRequest = { showFontColorPicker = false }
        )
    }

    if (showBackgroundColorPicker) {
        ColorPickerDialogs(
            initialColor = backgroundColor,
            onColorSelected = { selectedColor ->
                backgroundColor = selectedColor
                onColorValueChange(colorSchemeDetails.copy(backgroundColor = selectedColor.toArgb()))
                showBackgroundColorPicker = false
            },
            onDismissRequest = { showBackgroundColorPicker = false }
        )
    }
}

@Composable
fun ColorPickerDialogs(
    initialColor: Color,
    onColorSelected: (Color) -> Unit,
    onDismissRequest: () -> Unit
) {
    var selectedColor by remember { mutableStateOf(initialColor) }
    val controller = rememberColorPickerController()
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Color: ", style = MaterialTheme.typography.bodyMedium)
                AlphaTile(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    controller = controller
                )

                Text("Select Color", style = MaterialTheme.typography.bodyMedium)
                HsvColorPicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(450.dp)
                        .padding(10.dp),
                    controller = controller,
                    onColorChanged = { colorEnvelope ->
                        selectedColor = colorEnvelope.color
                    }
                )
                Text("Alpha", style = MaterialTheme.typography.bodyMedium)
                AlphaSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .height(35.dp),
                    controller = controller,
                    tileOddColor = Color.White,
                    tileEvenColor = Color.Black
                )
                Text("Brightness", style = MaterialTheme.typography.bodyMedium)
                BrightnessSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .height(35.dp),
                    controller = controller,
                )
                Button(onClick = {
                    onColorSelected(selectedColor)
                    onDismissRequest()
                }) {
                    Text("Done")
                }
            }
        }
    }
}


