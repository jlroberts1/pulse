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
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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

@Composable
fun NavigationScaffold(
    modifier: Modifier = Modifier,
    viewModel: AppViewModel,
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    unreadNotificationCount: Long,
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
                saveState = destination.route in TopDestinations.entries.map { it.route }
            }
            launchSingleTop = true
            restoreState = destination.route in TopDestinations.entries.map { it.route }
        }
    }

    val routeUiState =
        remember(currentRoute.value) {
            getRouteUiState(currentRoute.value, navController)
        }

    TopDestinations.entries.find { it.route == "notifications" }?.let {
        it.unreadCount = unreadNotificationCount
    }

    Scaffold(
        modifier =
            modifier
                .nestedScroll(nestedScrollConnection),
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                                    Box {
                                        Icon(
                                            imageVector =
                                                if (currentRoute.value == it.route) {
                                                    it.selectedIcon
                                                } else {
                                                    it.unselectedIcon
                                                },
                                            contentDescription = it.contentDescription,
                                        )
                                        it.unreadCount?.let {
                                            if (it > 0) {
                                                Badge(
                                                    modifier =
                                                        Modifier
                                                            .align(Alignment.TopEnd)
                                                            .offset(x = 6.dp, y = (-6).dp),
                                                ) {
                                                    Text(
                                                        text = if (it > 99) "99+" else it.toString(),
                                                        style = MaterialTheme.typography.labelSmall,
                                                    )
                                                }
                                            }
                                        }
                                    }
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
                    AnimatedVisibility(
                        visible = routeUiState.showBottomBar && mediaState.url == null,
                        enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn(),
                        exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut(),
                    ) {
                        NavigationRail {
                            TopDestinations.entries.forEach {
                                NavigationRailItem(
                                    icon = {
                                        Box {
                                            Icon(
                                                imageVector =
                                                    if (currentRoute.value == it.route) {
                                                        it.selectedIcon
                                                    } else {
                                                        it.unselectedIcon
                                                    },
                                                contentDescription = it.contentDescription,
                                            )
                                            it.unreadCount?.let {
                                                if (it > 0) {
                                                    Badge(
                                                        modifier =
                                                            Modifier
                                                                .align(Alignment.TopEnd)
                                                                .offset(x = 6.dp, y = (-6).dp),
                                                    ) {
                                                        Text(
                                                            text = if (it > 99) "99+" else it.toString(),
                                                            style = MaterialTheme.typography.labelSmall,
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    },
                                    selected = currentRoute.value == it.route,
                                    onClick = { onNavigate(it) },
                                    colors = NavigationRailItemDefaults.colors(),
                                )
                            }
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
                        drawerState = drawerState,
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
