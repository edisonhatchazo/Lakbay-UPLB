package com.edison.lakbayuplb.ui.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edison.lakbayuplb.R
import com.edison.lakbayuplb.ui.navigation.AppViewModelProvider
import com.edison.lakbayuplb.ui.navigation.NavigationDestination
import com.edison.lakbayuplb.ui.settings.global.TopAppBarColorSchemesViewModel
import com.edison.lakbayuplb.ui.theme.ThemeMode

object LakbayHomeScreenDestination: NavigationDestination {
    override val route = "about_lakbay"
    override val titleRes = R.string.app_name
}

@Composable
fun LakbayScreenAbout(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    colorViewModel: TopAppBarColorSchemesViewModel = viewModel(factory = AppViewModelProvider.Factory),
){
    val topAppBarColors = colorViewModel.topAppBarColors.collectAsState()
    val (topAppBarBackgroundColor, topAppBarForegroundColor) = topAppBarColors.value
    Scaffold(
        modifier = modifier,
        topBar = {
            AboutScreensTopAppBar(
                title = stringResource(R.string.app_name),
                url = R.drawable.lakbay_uplb_icon,
                navigateBack = navigateBack,
                topAppBarBackgroundColor = topAppBarBackgroundColor,
                topAppBarForegroundColor = topAppBarForegroundColor,
                description = "Lakbay UPLB Icon"
            )
        }
    ){ innerPadding ->
        AboutLakbayBody(innerPadding)
    }
}

@Composable
fun AboutLakbayBody(
    innerPadding: PaddingValues = PaddingValues(0.dp),
){
    Card(
        modifier = Modifier.padding(innerPadding),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier
                .padding(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
                .verticalScroll(rememberScrollState())
        ) {
            Text(text = lakbayStyledText(), textAlign = TextAlign.Justify)
        }
    }
}

@Composable
fun lakbayStyledText(): AnnotatedString {
    return buildAnnotatedString {
        val maroon = Color(0xFF800000) // Define the maroon color

        pushStyle(SpanStyle(fontSize = 18.sp))
        append("Welcome to ")
        pushStyle(SpanStyle(color = maroon, fontWeight = FontWeight.Bold, fontSize = 20.sp))
        append("Lakbay UPLB")
        pop()
        append(", your ultimate campus navigation companion! Designed to help students, faculty, and visitors explore University of the Philippines Los Baños.\n\n")

        pushStyle(SpanStyle(color = maroon, fontWeight = FontWeight.Bold, fontSize = 20.sp))
        append("Lakbay UPLB")
        pop()
        append(" provides an intuitive and seamless way to locate buildings, facilities, and key landmarks within the campus.\n\n")

        append("With ")
        pushStyle(SpanStyle(color = maroon, fontWeight = FontWeight.Bold, fontSize = 20.sp))
        append("Lakbay UPLB")
        pop()
        append(", you can:\n")

        append("📍 Easily find and navigate to different buildings\n")
        append("🔍 Search for specific locations by college or category\n")
        append("🗂 Access essential information about campus facilities\n\n")

        append("Whether you're a new student finding your way around or a visitor exploring the university, ")
        pushStyle(SpanStyle(color = maroon, fontWeight = FontWeight.Bold, fontSize = 20.sp))
        append("Lakbay UPLB")
        pop()
        append(" ensures that you reach your destination effortlessly.\n\n")

        append("Start your journey with ")
        pushStyle(SpanStyle(color = maroon, fontWeight = FontWeight.Bold, fontSize = 20.sp))
        append("Lakbay UPLB")
        pop()
        append(" today and experience hassle-free campus navigation!")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreensTopAppBar(
    title: String,
    url: Int,
    description: String,
    navigateBack: () -> Unit,
    topAppBarBackgroundColor: Color,
    topAppBarForegroundColor: Color
) {
    val selectedThemeMode by remember { mutableStateOf(ThemeMode.SYSTEM) }
    var showPopup by remember { mutableStateOf(false) }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = topAppBarBackgroundColor),
        title = { Text(title, color = topAppBarForegroundColor) },
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(onClick = navigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back_button),
                    tint = topAppBarForegroundColor
                )
            }
        },
        actions = {
            IconButton(onClick = { showPopup = true }) {
                Image(
                    painter = painterResource(R.drawable.image_icon),
                    contentDescription = description,
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(
                        if(selectedThemeMode == ThemeMode.DARK)
                            MaterialTheme.colorScheme.onSurface else Color.White
                    )
                )
            }
        }
    )
    if (showPopup) {
        PopupImageDialog(url = url, onClose = { showPopup = false })
    }
}
