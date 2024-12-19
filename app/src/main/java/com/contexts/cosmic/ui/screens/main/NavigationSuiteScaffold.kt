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
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.window.core.layout.WindowWidthSizeClass
import com.contexts.cosmic.ui.composables.ViewMedia

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationScaffold(
    modifier: Modifier = Modifier,
    viewModel: AppViewModel,
    navController: NavHostController,
    mediaState: MediaState,
    drawerState: DrawerState,
) {
    val scrollBehavior =
        rememberFabScrollBehavior(
            onScroll = { delta -> viewModel.updateControlsVisibility(delta) },
        )
    val scaffoldViewState by viewModel.scaffoldViewState.collectAsStateWithLifecycle()
    val controlsVisibility by viewModel.controlsVisibility.collectAsStateWithLifecycle()
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val isExpandedScreen =
        adaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: TopDestinations.HOME.route
    val showBottomBar =
        remember(navBackStackEntry) {
            navBackStackEntry?.destination?.route in TopDestinations.entries.map { it.route }
        }

    val onNavigate = { destination: TopDestinations ->
        navController.navigate(destination.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            if (scaffoldViewState.showTopAppBar && mediaState.url == null) {
                TopBar(
                    scaffoldViewState,
                    navController,
                    scrollBehavior.topBarBehavior,
                    drawerState,
                )
            }
        },
        floatingActionButton = {
            if (scaffoldViewState.showFab) {
                AnimatedVisibility(
                    visible = controlsVisibility == 1f && mediaState.url == null,
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
            if (!isExpandedScreen && showBottomBar) {
                AnimatedVisibility(
                    visible = controlsVisibility == 1f && mediaState.url == null,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    NavigationBar {
                        TopDestinations.entries.forEach {
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector =
                                            if (currentRoute == it.route) {
                                                it.selectedIcon
                                            } else {
                                                it.unselectedIcon
                                            },
                                        contentDescription = it.contentDescription,
                                    )
                                },
                                selected = currentRoute == it.route,
                                onClick = { onNavigate(it) },
                                colors = NavigationBarItemDefaults.colors(),
                            )
                        }
                    }
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
            modifier =
                Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(newPadding),
        ) {
            Row {
                if (isExpandedScreen) {
                    NavigationRail {
                        TopDestinations.entries.forEach {
                            NavigationRailItem(
                                icon = {
                                    Icon(
                                        imageVector =
                                            if (currentRoute == it.route) {
                                                it.selectedIcon
                                            } else {
                                                it.unselectedIcon
                                            },
                                        contentDescription = it.contentDescription,
                                    )
                                },
                                selected = currentRoute == it.route,
                                onClick = { onNavigate(it) },
                                colors = NavigationRailItemDefaults.colors(),
                            )
                        }
                    }
                }

                Box(
                    modifier =
                        Modifier
                            .fillMaxSize(),
                ) {
                    RootNav(
                        navController,
                        updateScaffoldViewState = { viewModel.updateScaffoldViewState(it) },
                        controlsVisibility = controlsVisibility,
                        onMediaOpen = { viewModel.onMediaOpen(it) },
                    )
                }
            }
            AnimatedVisibility(
                visible = mediaState.url != null,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                mediaState.url?.let { url ->
                    ViewMedia(
                        mediaUrl = url,
                        onDismiss = { viewModel.onMediaDismissed() },
                    )
                }
            }
        }
    }
}
