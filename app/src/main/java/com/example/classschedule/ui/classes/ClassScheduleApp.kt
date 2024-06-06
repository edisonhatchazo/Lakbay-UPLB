@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.classschedule.ui.classes

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.classschedule.R
import com.example.classschedule.ui.navigation.ClassScheduleNavHost

@Composable
fun ClassScheduleApp(navController: NavHostController = rememberNavController()) {
    ClassScheduleNavHost(navController = navController)
}


@Composable
fun ClassScheduleTopAppBar(
    title:String,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateUp: () -> Unit = {}
){
    CenterAlignedTopAppBar(
        title = { Text( title)},
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if(canNavigateBack){
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription  = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}