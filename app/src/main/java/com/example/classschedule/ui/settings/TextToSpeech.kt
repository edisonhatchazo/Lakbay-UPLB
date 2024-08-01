package com.example.classschedule.ui.settings

import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import com.example.classschedule.R
import com.example.classschedule.ui.navigation.NavigationDestination
import com.example.classschedule.ui.settings.global.DirectoryTopAppBar
import java.util.Locale

object TextToSpeechDestination: NavigationDestination {
    override val route = "text_t_speech"
    override val titleRes = R.string.text_to_speech
}


@Composable
fun TextToSpeechMainScreen(
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
) {
    Scaffold(
        topBar = {
            DirectoryTopAppBar(
                title = stringResource(TextToSpeechDestination.titleRes),
                canNavigateBack = canNavigateBack,
                onNavigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        TextToSpeechScreen(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
    }
}

@Composable
fun TextToSpeechScreen(
    modifier: Modifier
){
    val context = LocalContext.current
    var textState by remember { mutableStateOf(TextFieldValue("")) }
    val tts = remember {
        TextToSpeech(context) { status ->
            if (status != TextToSpeech.SUCCESS) {
                Toast.makeText(context, "TTS Initialization failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    DisposableEffect(Unit) {
        tts.language = Locale.US
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        OutlinedTextField(
            value = textState,
            onValueChange = { textState = it },
            label = { Text(stringResource(R.string.text_to_speech)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Button(
            onClick = {
                val text = textState.text
                if (text.isNotEmpty()) {
                    tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
                }
            },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "To Speech")
        }
    }
}