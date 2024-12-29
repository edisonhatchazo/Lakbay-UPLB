package com.edison.lakbayuplb.algorithm

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edison.lakbayuplb.ui.navigation.AppViewModelProvider
import com.edison.lakbayuplb.ui.settings.colors.ColorSchemeHomeViewModel

@Composable
fun ColorPickerDialog(
    onColorSelected: (Int) -> Unit,
    onDismiss: () -> Unit,
    viewModel: ColorSchemeHomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val colors by viewModel.existingColors.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Select Color") },
        text = {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(8.dp)
            ) {

                items(colors) { colorScheme ->
                Box(
                    modifier = Modifier
                        .height(60.dp)
                        .background(color = Color(colorScheme.backgroundColor))
                        .clickable {onColorSelected(colorScheme.id)}
                        .padding(8.dp),
                    contentAlignment = Alignment.Center,


                ) {
                    Text(
                        colorScheme.name,
                        color = Color(colorScheme.fontColor),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}