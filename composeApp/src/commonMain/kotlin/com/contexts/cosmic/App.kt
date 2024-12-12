/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.sharp.Add
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.contexts.cosmic.domain.model.NavigationIcon
import com.contexts.cosmic.ui.components.FabScrollBehavior
import com.contexts.cosmic.ui.components.SnackbarDelegate
import com.contexts.cosmic.ui.composables.AddPostBottomSheet
import com.contexts.cosmic.ui.composables.SnackbarHost
import com.contexts.cosmic.ui.composables.Splash
import com.contexts.cosmic.ui.composables.TopBar
import com.contexts.cosmic.ui.theme.CosmicTheme
import com.materialkolor.rememberDynamicMaterialThemeState
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App(
    viewModel: MainViewModel = koinViewModel(),
    snackbarDelegate: SnackbarDelegate = koinInject(),
) {
    val isDarkTheme = isSystemInDarkTheme()
    val isAmoled by rememberSaveable { mutableStateOf(false) }
    val color = Color(0xFF0023FF).toArgb()
    val seedColor by rememberSaveable { mutableStateOf(color) }
    val snackbarHostState = remember { SnackbarHostState() }
    val authState by viewModel.authState.collectAsState()

    val scaffoldViewState by viewModel.scaffoldViewState.collectAsState()
    snackbarDelegate.apply {
        this.snackbarHostState = snackbarHostState
        coroutineScope = rememberCoroutineScope()
    }

    val bottomSheetVisible by viewModel.bottomSheetVisible.collectAsState()

    val themeState =
        rememberDynamicMaterialThemeState(
            seedColor = Color(seedColor),
            isDark = isDarkTheme,
            isAmoled = isAmoled,
        )

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val showBottomBar =
        remember(navBackStackEntry) {
            navBackStackEntry?.destination?.route in bottomNavDestinations.map { it.route }
        }

    val controlsVisibility by viewModel.controlsVisibility.collectAsState()

    val scrollBehavior =
        rememberFabScrollBehavior(
            onScroll = { delta -> viewModel.updateControlsVisibility(delta) },
        )

    CosmicTheme(themeState) {
        KoinContext {
            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    if (scaffoldViewState.showTopAppBar) {
                        TopBar(scaffoldViewState, navController, scrollBehavior.topBarBehavior)
                    }
                },
                snackbarHost = { SnackbarHost(snackbarHostState, snackbarDelegate) },
                floatingActionButton = {
                    if (scaffoldViewState.showFab) {
                        FloatingActionButton(
                            onClick = { viewModel.showBottomSheet() },
                            containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                            modifier =
                                Modifier.graphicsLayer {
                                    alpha = controlsVisibility
                                    translationY = 100f * (1f - controlsVisibility)
                                },
                        ) {
                            Icon(Icons.Sharp.Add, "Add")
                        }
                    }
                },
                bottomBar = {
                    AnimatedVisibility(
                        visible = showBottomBar,
                        modifier =
                            Modifier.graphicsLayer {
                                alpha = controlsVisibility
                                translationY = 100f * (1f - controlsVisibility)
                            },
                    ) {
                        NavigationBar(
                            modifier = Modifier.height(72.dp),
                        ) {
                            val currentRoute = navBackStackEntry?.destination?.route
                            bottomNavDestinations.forEach { screen ->
                                val selected = currentRoute == screen.route
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = {
                                        navController.navigate(screen.route) {
                                            navController.graph.startDestinationRoute?.let {
                                                popUpTo(it) { saveState = true }
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = {
                                        val icon = getIconForScreen(screen)
                                        Icon(
                                            icon.image,
                                            contentDescription = icon.contentDescription,
                                        )
                                    },
                                )
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
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    AnimatedContent(
                        targetState = authState,
                    ) { state ->
                        when (state) {
                            AuthenticationState.Loading -> {
                                if (getPlatform().isIOS()) {
                                    Splash()
                                }
                            }

                            AuthenticationState.Authenticated ->
                                AuthenticatedNavigation(
                                    navController,
                                    updateScaffoldViewState = { viewModel.updateScaffoldViewState(it) },
                                    modifier = Modifier.padding(newPadding),
                                )

                            AuthenticationState.Unauthenticated ->
                                UnauthenticatedNavigation(
                                    navController,
                                    updateScaffoldViewState = { viewModel.updateScaffoldViewState(it) },
                                    modifier = Modifier.padding(newPadding),
                                )
                        }
                    }

                    if (bottomSheetVisible) {
                        AddPostBottomSheet(
                            onDismiss = { viewModel.hideBottomSheet() },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun getIconForScreen(screen: NavigationRoutes): NavigationIcon {
    return when (screen) {
        NavigationRoutes.Authenticated.Home -> NavigationIcon(Icons.Default.Home, "Home")
        NavigationRoutes.Authenticated.Search -> NavigationIcon(Icons.Default.Search, "Search")
        NavigationRoutes.Authenticated.Messages ->
            NavigationIcon(
                Icons.AutoMirrored.Filled.Message,
                "Messages",
            )

        NavigationRoutes.Authenticated.Notifications ->
            NavigationIcon(
                Icons.Default.Notifications,
                "Notifications",
            )

        NavigationRoutes.Authenticated.Profile -> NavigationIcon(Icons.Default.Person, "Profile")
        else -> NavigationIcon(Icons.Default.Home, "Home")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberFabScrollBehavior(onScroll: (Float) -> Unit): FabScrollBehavior {
    val topBarState = rememberTopAppBarState()
    val topBarBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topBarState)

    return remember(topBarBehavior) {
        FabScrollBehavior(topBarBehavior, onScroll)
    }
}

val bottomNavDestinations =
    listOf(
        NavigationRoutes.Authenticated.Home,
        NavigationRoutes.Authenticated.Search,
        NavigationRoutes.Authenticated.Messages,
        NavigationRoutes.Authenticated.Notifications,
        NavigationRoutes.Authenticated.Profile,
    )
