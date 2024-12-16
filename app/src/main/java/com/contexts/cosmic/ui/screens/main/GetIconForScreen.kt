/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.ui.screens.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun getIconForScreen(screen: NavigationRoutes): NavigationIcon {
    return when (screen) {
        NavigationRoutes.Authenticated.Home -> NavigationIcon(Icons.Default.Home, "Home")
        NavigationRoutes.Authenticated.Search -> NavigationIcon(Icons.Default.Search, "Search")
        NavigationRoutes.Authenticated.Chat -> NavigationIcon(Icons.AutoMirrored.Filled.Message, "Chat")
        NavigationRoutes.Authenticated.Notifications -> NavigationIcon(Icons.Default.Notifications, "Notifications")
        NavigationRoutes.Authenticated.Profile -> NavigationIcon(Icons.Default.Person, "Profile")
        else -> NavigationIcon(Icons.Default.Home, "Home")
    }
}

data class NavigationIcon(
    val image: ImageVector,
    val contentDescription: String,
)
