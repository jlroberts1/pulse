/*
 * Copyright (c) 2025. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.composables

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.NavController
import androidx.window.core.layout.WindowWidthSizeClass
import com.contexts.pulse.ui.screens.main.TopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldedScreen(
    navController: NavController,
    title: String,
    drawerState: DrawerState,
    actions: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val isExpandedScreen =
        adaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED
    val topAppBarScrollBehavior =
        if (isExpandedScreen) {
            TopAppBarDefaults.pinnedScrollBehavior()
        } else {
            TopAppBarDefaults.enterAlwaysScrollBehavior()
        }

    Scaffold(
        modifier =
            Modifier
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopBar(
                navController = navController,
                title = title,
                scrollBehavior = topAppBarScrollBehavior,
                drawerState = drawerState,
                actions = actions,
            )
        },
        floatingActionButton = floatingActionButton,
    ) { padding ->
        content(padding)
    }
}
