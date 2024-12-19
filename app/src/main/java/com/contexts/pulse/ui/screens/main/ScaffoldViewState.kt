/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.screens.main

import androidx.compose.runtime.Composable

data class ScaffoldViewState(
    val topAppBarTitle: String = "Pulse",
    val topBarActions: @Composable () -> Unit = {},
    val fabIcon: @Composable () -> Unit = {},
    val fabAction: () -> Unit = {},
    val showTopAppBar: Boolean = true,
    val showFab: Boolean = true,
    val isRefreshing: Boolean = false,
    val controlsVisibility: Float = 1f,
)
