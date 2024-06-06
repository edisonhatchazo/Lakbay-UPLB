package com.example.classschedule.ui.theme

import androidx.compose.ui.graphics.Color

data class ColorEntry(val backgroundColor: Color, val fontColor: Color)

object ColorPalette {
    val colors = mapOf(
        "Black" to ColorEntry(Color.Black, Color.White),
        "Blue" to ColorEntry(Color.Blue, Color.White),
        "Green" to ColorEntry(Color.Green, Color.Black),
        "Purple" to ColorEntry(Color(0xFF800080), Color.White), // Purple
        "Dark Yellow" to ColorEntry(Color(0xFF808000), Color.Black), // Dark Yellow
        "Magenta" to ColorEntry(Color.Magenta, Color.Black),
        "Cyan" to ColorEntry(Color.Cyan, Color.Black),
        "Orange" to ColorEntry(Color(0xFFFFA500), Color.Black), // Orange
        "Yellow" to ColorEntry(Color.Yellow, Color.Black),
        "Gray" to ColorEntry(Color.Gray, Color.Black),
        "Brown" to ColorEntry(Color(0xFFA52A2A), Color.White), // Brown
        "Pink" to ColorEntry(Color(0xFFFFC0CB), Color.Black), // Pink
        "Indigo" to ColorEntry(Color(0xFF4B0082), Color.White), // Indigo
        "Navy Blue" to ColorEntry(Color(0xFF000080), Color.White), // Navy Blue
        "Bronze" to ColorEntry(Color(0xFFCD7F32), Color.Black), // Bronze
        "Silver" to ColorEntry(Color(0xFFC0C0C0), Color.Black), // Silver
        "Gold" to ColorEntry(Color(0xFFFFD700), Color.Black), // Gold
        "Copper Rose" to ColorEntry(Color(0xFF996666), Color.Black), // Copper Rose
        "Turquoise" to ColorEntry(Color(0xFF40E0D0), Color.Black), // Turquoise
        "Pearl" to ColorEntry(Color(0xFFF0EAD6), Color.Black), // Pearl
        "Lemon" to ColorEntry(Color(0xFFFFE700), Color.Black), // Lemon
        "Lavender" to ColorEntry(Color(0xFFE6E6FA), Color.Black), // Lavender
        "Mustard" to ColorEntry(Color(0xFFFFDB58), Color.Black), // Mustard
        "Emerald" to ColorEntry(Color(0xFF50C878), Color.Black), // Emerald
        "Chocolate" to ColorEntry(Color(0xFF7B3F00), Color.White) // Chocolate
    )
}

fun getColorEntry(colorName: String): ColorEntry {
    return ColorPalette.colors[colorName] ?: ColorEntry(Color.Transparent, Color.Black)
}