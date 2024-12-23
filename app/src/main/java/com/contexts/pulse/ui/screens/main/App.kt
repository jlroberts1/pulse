/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.screens.main

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.contexts.pulse.ui.theme.AppTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App(viewModel: AppViewModel = koinViewModel()) {
    val navController = rememberNavController()
    val theme by viewModel.theme.collectAsStateWithLifecycle()
    val mediaState by viewModel.mediaState.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarMessage by viewModel.snackbarMessages.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.navigationEvent) {
        viewModel.navigationEvent.collect { route ->
            if (navController.currentBackStackEntry?.destination?.parent?.route != route) {
                navController.navigate(route) {
                    popUpTo(
                        if (route == NavigationRoutes.Authenticated.NavigationRoute.route) {
                            NavigationRoutes.Unauthenticated.NavigationRoute.route
                        } else {
                            NavigationRoutes.Authenticated.NavigationRoute.route
                        },
                    ) {
                        inclusive = true
                    }
                }
            }
        }
    }
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }
    AppTheme(theme = theme) {
        NavigationDrawer(
            profile = profile,
            navController = navController,
            drawerState = drawerState,
        ) {
            NavigationScaffold(
                viewModel = viewModel,
                navController = navController,
                mediaState = mediaState,
                drawerState = drawerState,
                snackbarHostState = snackbarHostState,
            )
        }
    }
}
