/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.ui.screens.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    scaffoldViewState: ScaffoldViewState,
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    TopAppBar(
        colors =
            TopAppBarDefaults.topAppBarColors(),
        scrollBehavior = scrollBehavior,
        title = { Text(scaffoldViewState.topAppBarTitle) },
        actions = { scaffoldViewState.topBarActions() },
        navigationIcon = {
            val topLevelRoutes = TopDestinations.entries.map { it.route }
            if (navController.previousBackStackEntry != null &&
                !topLevelRoutes.contains(navController.currentDestination?.route)
            ) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                    )
                }
            }
        },
    )
}
