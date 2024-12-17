/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.ui.screens.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Add
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.contexts.cosmic.ui.theme.AppTheme
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(viewModel: AppViewModel = koinViewModel()) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val theme by viewModel.theme.collectAsStateWithLifecycle()
    val scaffoldViewState by viewModel.scaffoldViewState.collectAsStateWithLifecycle()
    val showBottomBar =
        remember(navBackStackEntry) {
            navBackStackEntry?.destination?.route in topLevelDestinations.map { it.route }
        }
    val controlsVisibility by viewModel.controlsVisibility.collectAsStateWithLifecycle()

    val scrollBehavior =
        rememberFabScrollBehavior(
            onScroll = { delta -> viewModel.updateControlsVisibility(delta) },
        )
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    LaunchedEffect(isLoggedIn) {
        isLoggedIn?.let {
            if (it) {
                navController.navigate(NavigationRoutes.Authenticated.NavigationRoute.route) {
                    popUpTo(NavigationRoutes.Unauthenticated.NavigationRoute.route) {
                        inclusive = true
                    }
                }
            } else {
                navController.navigate(NavigationRoutes.Unauthenticated.NavigationRoute.route) {
                    popUpTo(NavigationRoutes.Authenticated.NavigationRoute.route) {
                        inclusive = true
                    }
                }
            }
        }
    }
    AppTheme(theme = theme) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                if (scaffoldViewState.showTopAppBar) {
                    TopBar(
                        scaffoldViewState,
                        navController,
                        scrollBehavior.topBarBehavior,
                    )
                }
            },
            floatingActionButton = {
                if (scaffoldViewState.showFab) {
                    AnimatedVisibility(
                        visible = controlsVisibility == 1f,
                        enter = scaleIn(),
                        exit = scaleOut(),
                    ) {
                        FloatingActionButton(
                            onClick = {
                                scaffoldViewState.fabAction()
                            },
                            containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                        ) {
                            Icon(Icons.Sharp.Add, "Add")
                        }
                    }
                }
            },
            bottomBar = {
                if (showBottomBar) {
                    AnimatedVisibility(
                        visible = controlsVisibility == 1f,
                        enter = slideInVertically { it },
                        exit = slideOutVertically { it },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        BottomBar(
                            currentRoute = navBackStackEntry?.destination?.route,
                            navController = navController,
                        )
                    }
                }
            },
        ) { innerPadding ->
            val newPadding =
                PaddingValues(
                    top = innerPadding.calculateTopPadding(),
                    bottom = 0.dp,
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                )
            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                RootNav(
                    navController,
                    updateScaffoldViewState = { viewModel.updateScaffoldViewState(it) },
                    controlsVisibility = controlsVisibility,
                    modifier =
                        Modifier
                            .imePadding()
                            .navigationBarsPadding()
                            .padding(newPadding),
                )
            }
        }
    }
}
