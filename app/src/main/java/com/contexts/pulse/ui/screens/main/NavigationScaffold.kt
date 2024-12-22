/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.screens.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.window.core.layout.WindowWidthSizeClass
import com.contexts.pulse.ui.composables.ViewMedia

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationScaffold(
    modifier: Modifier = Modifier,
    viewModel: AppViewModel,
    navController: NavHostController,
    mediaState: MediaState,
    drawerState: DrawerState,
) {
    var controlsVisible by remember { mutableStateOf(true) }
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val isExpandedScreen =
        adaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED
    val currentRoute =
        rememberUpdatedState(
            navController.currentBackStackEntryAsState().value?.destination?.route
                ?: TopDestinations.HOME.route,
        )
    val topAppBarScrollBehavior =
        if (isExpandedScreen) {
            TopAppBarDefaults.pinnedScrollBehavior()
        } else {
            TopAppBarDefaults.enterAlwaysScrollBehavior()
        }
    val nestedScrollConnection =
        remember {
            object : NestedScrollConnection {
                override fun onPreScroll(
                    available: Offset,
                    source: NestedScrollSource,
                ): Offset {
                    if (available.y < -10) {
                        controlsVisible = false
                    } else if (available.y > 10) {
                        controlsVisible = true
                    }
                    return Offset.Zero
                }
            }
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

    val routeUiState =
        remember(currentRoute.value) {
            getRouteUiState(currentRoute.value, navController)
        }

    Scaffold(
        modifier =
            modifier
                .nestedScroll(nestedScrollConnection)
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            if (routeUiState.showTopAppBar && mediaState.url == null) {
                TopBar(
                    navController = navController,
                    title = routeUiState.topAppBarTitle,
                    scrollBehavior = topAppBarScrollBehavior,
                    actions = routeUiState.topBarActions,
                    drawerState = drawerState,
                )
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = routeUiState.showFab && controlsVisible && mediaState.url == null,
                enter = scaleIn(),
                exit = scaleOut(),
            ) {
                FloatingActionButton(
                    onClick = { routeUiState.fabAction() },
                    containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                ) {
                    Icon(routeUiState.fabIcon, routeUiState.fabDesc)
                }
            }
        },
        bottomBar = {
            if (!isExpandedScreen) {
                AnimatedVisibility(
                    visible = routeUiState.showBottomBar && controlsVisible && mediaState.url == null,
                    modifier = Modifier.fillMaxWidth(),
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                ) {
                    NavigationBar {
                        TopDestinations.entries.forEach {
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector =
                                            if (currentRoute.value == it.route) {
                                                it.selectedIcon
                                            } else {
                                                it.unselectedIcon
                                            },
                                        contentDescription = it.contentDescription,
                                    )
                                },
                                selected = currentRoute.value == it.route,
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
                                            if (currentRoute.value == it.route) {
                                                it.selectedIcon
                                            } else {
                                                it.unselectedIcon
                                            },
                                        contentDescription = it.contentDescription,
                                    )
                                },
                                selected = currentRoute.value == it.route,
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
