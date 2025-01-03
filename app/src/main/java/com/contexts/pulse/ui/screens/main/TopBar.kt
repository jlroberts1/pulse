/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.screens.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navController: NavController,
    title: String,
    actions:
        @Composable()
        (() -> Unit?)? = null,
    scrollBehavior: TopAppBarScrollBehavior,
    drawerState: DrawerState,
) {
    val scope = rememberCoroutineScope()
    TopAppBar(
        colors =
            TopAppBarDefaults.topAppBarColors(),
        scrollBehavior = scrollBehavior,
        title = { Text(title) },
        actions = {
            if (actions != null) {
                actions()
            }
        },
        navigationIcon = {
            val topLevelRoutes = TopDestinations.entries.map { it.route }
            if (navController.previousBackStackEntry != null &&
                !topLevelRoutes.contains(navController.currentDestination?.route)
            ) {
                IconButton(onClick = {
                    scope.launch {
                        if (!navController.popBackStack()) {
                            navController.navigateUp()
                        }
                    }
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                    )
                }
            } else {
                IconButton(onClick = {
                    scope.launch {
                        drawerState.apply {
                            if (isClosed) open() else close()
                        }
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                    )
                }
            }
        },
    )
}
