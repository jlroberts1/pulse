package com.contexts.cosmic.ui.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.contexts.cosmic.ScaffoldViewState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    scaffoldViewState: ScaffoldViewState,
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    TopAppBar(
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
        scrollBehavior = scrollBehavior,
        title = { Text(scaffoldViewState.topAppBarTitle) },
        actions = { scaffoldViewState.topBarActions() },
        navigationIcon = {
            if (navController.previousBackStackEntry != null) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }
            } else {
                null
            }
        },
    )
}
