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
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

enum class TopDestinations(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String,
) {
    HOME(NavigationRoutes.Authenticated.Home.route, "Home", Icons.Default.Home, "Home"),
    SEARCH(NavigationRoutes.Authenticated.Search.route, "Search", Icons.Default.Search, "Search"),
    CHAT(NavigationRoutes.Authenticated.Chat.route, "Chat", Icons.Default.ChatBubble, "Chat"),
    NOTIFICATIONS(NavigationRoutes.Authenticated.Notifications.route, "Notifications", Icons.Default.Notifications, "Notifications"),
    PROFILE(NavigationRoutes.Authenticated.Profile.route, "Profile", Icons.Default.Person, "Profile"),
}
