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
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
    val unreadNotificationCount by viewModel.unreadNotificationCount.collectAsStateWithLifecycle()
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
    AppTheme(theme = theme) {
        NavigationDrawer(
            profile = profile,
            navController = navController,
            unreadNotificationCount = unreadNotificationCount,
            drawerState = drawerState,
        ) {
            NavigationScaffold(
                viewModel = viewModel,
                navController = navController,
                unreadNotificationCount = unreadNotificationCount,
                mediaState = mediaState,
                drawerState = drawerState,
            )
        }
    }
}
