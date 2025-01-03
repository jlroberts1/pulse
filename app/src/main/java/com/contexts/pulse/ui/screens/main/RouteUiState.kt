/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.screens.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

data class RouteUiState(
    val topAppBarTitle: String = "Pulse",
    val topBarActions: @Composable ((RouteAction) -> Unit) -> Unit? = {},
    val fabIcon: ImageVector = Icons.Sharp.Add,
    val fabDesc: String? = "Add new post",
    val fabAction: () -> Unit = {},
    val showTopAppBar: Boolean = true,
    val showFab: Boolean = false,
    val showBottomBar: Boolean = false,
)

sealed interface RouteAction {
    data object FeedConfig : RouteAction
}
