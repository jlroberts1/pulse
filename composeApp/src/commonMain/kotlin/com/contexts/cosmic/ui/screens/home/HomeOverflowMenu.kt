/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.ui.screens.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun HomeOverflowMenu(action: (HomeOverflowMenuAction) -> Unit) {
    var showMenu by remember { mutableStateOf(false) }

    IconButton(onClick = {
        showMenu = !showMenu
    }) {
        Icon(
            imageVector = Icons.Outlined.MoreVert,
            contentDescription = "More",
        )
    }
    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = { showMenu = false },
    ) {
        DropdownMenuItem(
            text = { Text("Settings") },
            onClick = { action(HomeOverflowMenuAction.Settings) },
        )
        DropdownMenuItem(
            text = { Text("Log out") },
            onClick = { action(HomeOverflowMenuAction.Logout) },
        )
    }
}

sealed class HomeOverflowMenuAction {
    data object Settings : HomeOverflowMenuAction()

    data object Logout : HomeOverflowMenuAction()
}
