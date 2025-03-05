package com.edison.lakbayuplb.ui.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.edison.lakbayuplb.R
import com.edison.lakbayuplb.ui.theme.ThemeMode


@Composable
fun PopupImageDialog(url: Int, onClose: () -> Unit) {
    Dialog(onDismissRequest = { onClose() }) {
        Box{
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentHeight()
                    .align(Alignment.Center)
            ) {
                val image = painterResource(id = url)

                IconButton(
                    onClick = { onClose() },
                    modifier = Modifier
                        .align(Alignment.End)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }

                Image(
                    painter = image,
                    contentDescription = "Popup Image",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}


@Composable
fun TextAndIcon(
    url: Int,
    description: String
){
    var showPopup by remember { mutableStateOf(false) }
    val selectedThemeMode by remember { mutableStateOf(ThemeMode.SYSTEM) }

    Text(
        text = description,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.ExtraBold
    )
    IconButton(
        onClick = { showPopup = true }
    ) {
        Image(
            painter = painterResource(R.drawable.image_icon),
            contentDescription = description,
            modifier = Modifier.size(18.dp),
            colorFilter = ColorFilter.tint(
                if(selectedThemeMode == ThemeMode.DARK)
                    MaterialTheme.colorScheme.onSurface else Color.White
            )
        )
    }

    if (showPopup) {
        PopupImageDialog(url = url, onClose = { showPopup = false })
    }
}

@Composable
fun TitleTextAndIcon(
    url: Int,
    description: String
){
    var showPopup by remember { mutableStateOf(false) }
    val selectedThemeMode by remember { mutableStateOf(ThemeMode.SYSTEM) }

    Text(
        text = description,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.ExtraBold
    )
    IconButton(
        onClick = { showPopup = true }
    ) {
        Image(
            painter = painterResource(R.drawable.image_icon),
            contentDescription = description,
            modifier = Modifier.size(18.dp),
            colorFilter = ColorFilter.tint(
                if(selectedThemeMode == ThemeMode.DARK)
                    MaterialTheme.colorScheme.onSurface else Color.White
            )
        )
    }

    if (showPopup) {
        PopupImageDialog(url = url, onClose = { showPopup = false })
    }
}

@Composable
fun ClassesDetailsDescription(
    title: String,
    description: String
){
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp,bottom = 8.dp),
        color = Color.Gray,
        thickness = 2.dp
    )
    Text(
        textAlign = TextAlign.Center,
        text = title,
        fontWeight = FontWeight.ExtraBold
    )
    Text(
        textAlign = TextAlign.Center,
        text = description
    )
}

@Composable
fun ClassesDescription(
    url: Int,
    description: String,
    text: String
){
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        color = Color.Gray,
        thickness = 2.dp
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
        TextAndIcon(
            url = url,
            description = description
        )
    }
    Text(
        textAlign = TextAlign.Center,
        text = text
    )
}


@Composable
fun JustifyDescription(
    url: Int,
    description: String,
    text: String
){
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        color = Color.Gray,
        thickness = 2.dp
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
        TextAndIcon(
            url = url,
            description = description
        )
    }
    Text(
        textAlign = TextAlign.Justify,
        text = text
    )
}

