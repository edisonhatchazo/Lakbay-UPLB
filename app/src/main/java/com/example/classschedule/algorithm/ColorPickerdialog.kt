package com.example.classschedule.algorithm

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.classschedule.ui.theme.ColorPalette

@Composable
fun ColorPickerDialog(
    onColorSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Select Color") },
        text = {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(ColorPalette.colors.entries.toList()) { (colorName, colorEntry) ->
                    Box(
                        modifier = Modifier
                            .height(60.dp)
                            .background(color = colorEntry.backgroundColor)
                            .clickable { onColorSelected(colorName) }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(colorName,
                            color = colorEntry.fontColor,
                            fontSize = 14.sp)
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