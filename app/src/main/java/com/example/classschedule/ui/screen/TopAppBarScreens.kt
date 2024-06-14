package com.example.classschedule.ui.screen

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.classschedule.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleEntryScreenTopAppBar(
    title:String,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit = {},
){
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Blue),
        title = { Text( title,color = Color.White)},
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if(canNavigateBack){
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription  = stringResource(R.string.back_button),
                        tint = Color.White
                    )
                }
            }
        },
        actions = {

            IconButton(
                onClick = { /*TODO*/ }
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = stringResource(R.string.about),
                    tint = Color.White
                )
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreenTopAppBar(
    title:String,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    navigateToScheduleEntry: () -> Unit,
    navigateToClassHome: () -> Unit,
    navigateToExamHome: () -> Unit,
    navigateUp: () -> Unit = {},

){
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var showMenu  by remember { mutableStateOf(false) }
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Blue),
        title = { Text( title,color = Color.White)},
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if(canNavigateBack){
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription  = stringResource(R.string.back_button),
                        tint = Color.White
                    )
                }
            }
        },
        actions = {
            IconButton(
                onClick = { showMenu = !showMenu}
            ){
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Menu",
                    tint = Color.White
                )
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    onClick = {
                        navigateToClassHome()
                        showMenu = false
                    },
                    text = {Text("Class")}
                )
                DropdownMenuItem(
                    onClick = {
                        navigateToExamHome()
                        showMenu = false
                    },
                    text = {Text("Exam")}
                )
            }

            IconButton(
                onClick = navigateToScheduleEntry
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription =  stringResource(R.string.class_entry_title),
                    tint = Color.Yellow
                )
            }



            IconButton(
                onClick = { /*TODO*/ }
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = stringResource(R.string.about),
                    tint = Color.White
                )
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreenTopAppBar(
    title:String,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit = {},
){
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Blue),
        title = { Text( title,color = Color.White)},
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if(canNavigateBack){
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription  = stringResource(R.string.back_button),
                        tint = Color.White
                    )
                }
            }
        },
        actions = {

            IconButton(
                onClick = { /*TODO*/ }
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = stringResource(R.string.about),
                    tint = Color.White
                )
            }
        },
    )
}