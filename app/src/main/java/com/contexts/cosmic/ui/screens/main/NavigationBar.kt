/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.ui.screens.main

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun BottomBar(
    currentRoute: String?,
    navController: NavController,
) {
    NavigationBar(
        modifier = Modifier.height(72.dp),
    ) {
        topLevelDestinations.forEach { screen ->
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
//                    if (screen.route == NavigationRoutes.Authenticated.Notifications.route) {
//                        BadgedBox(
//                            badge = {
//                                if (unreadCount != 0L) {
//                                    Badge {
//                                        Text(text = unreadCount.toString())
//                                    }
//                                }
//                            },
//                        ) {
//                            Icon(
//                                imageVector = icon.image,
//                                contentDescription = icon.contentDescription,
//                            )
//                        }
//                    } else {
                    Icon(
                        imageVector = icon.image,
                        contentDescription = icon.contentDescription,
                    )
//                    }
                },
            )
        }
    }
}
