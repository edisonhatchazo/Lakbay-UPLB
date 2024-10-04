package com.edison.lakbayuplb

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.edison.lakbayuplb.data.AppContainer
import com.edison.lakbayuplb.data.AppDataContainer
import com.edison.lakbayuplb.ui.navigation.Navigation
import com.edison.lakbayuplb.ui.navigation.NavigationDrawerContent
import com.edison.lakbayuplb.ui.theme.ThemeMode
import kotlinx.coroutines.launch

class LakbayUPLBApplication : Application() {

    lateinit var container: AppContainer
    override fun onCreate(){
        super.onCreate()
        container = AppDataContainer(this)

    }
}

@Composable
fun LakbayApp(onThemeChange: (ThemeMode) -> Unit) {
    val mainNavController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()



    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = false,
        drawerContent = {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(240.dp)  // Fixed width for the drawer content
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                NavigationDrawerContent(
                    navController = mainNavController,
                    closeDrawer = { coroutineScope.launch { drawerState.close() } }
                )
            }
        },
        content = {
            Navigation(
                navController = mainNavController,
                openDrawer = { coroutineScope.launch { drawerState.open() } },
                onThemeChange = onThemeChange
            )
        }
    )
}
