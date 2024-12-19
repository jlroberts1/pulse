/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.ExpandCircleDown
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.ChatBubble
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.contexts.pulse.data.local.database.entities.ProfileEntity
import com.contexts.pulse.ui.composables.BorderedCircularAvatar
import kotlinx.coroutines.launch

data class NavigationItems(
    val title: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeCount: Int? = null,
)

@Composable
fun NavigationDrawer(
    profile: ProfileEntity?,
    accounts: List<String>? = listOf(""),
    navController: NavController,
    drawerState: DrawerState,
    content: @Composable () -> Unit,
) {
    val onNavigate = { route: String ->
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val currentRoute =
        rememberUpdatedState(
            navController.currentBackStackEntryAsState().value?.destination?.route
                ?: TopDestinations.HOME.route,
        )

    val items =
        remember {
            listOf(
                NavigationItems(
                    title = "Home",
                    route = NavigationRoutes.Authenticated.Home.route,
                    selectedIcon = Icons.Filled.Home,
                    unselectedIcon = Icons.Outlined.Home,
                ),
                NavigationItems(
                    title = "Search",
                    route = NavigationRoutes.Authenticated.Search.route,
                    selectedIcon = Icons.Filled.Search,
                    unselectedIcon = Icons.Outlined.Search,
                ),
                NavigationItems(
                    title = "Chat",
                    route = NavigationRoutes.Authenticated.Chat.route,
                    selectedIcon = Icons.Filled.ChatBubble,
                    unselectedIcon = Icons.Outlined.ChatBubble,
                ),
                NavigationItems(
                    title = "Notifications",
                    route = NavigationRoutes.Authenticated.Notifications.route,
                    selectedIcon = Icons.Filled.Notifications,
                    unselectedIcon = Icons.Outlined.Notifications,
                ),
                NavigationItems(
                    title = "Profile",
                    route = NavigationRoutes.Authenticated.Profile.route,
                    selectedIcon = Icons.Filled.Person,
                    unselectedIcon = Icons.Outlined.Person,
                ),
            )
        }
    var selectedItemIndex by rememberSaveable {
        mutableIntStateOf(0)
    }
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp),
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    accounts?.size?.let {
                        if (it > 1) {
                            IconButton(
                                onClick = {
                                    // switch account
                                },
                                modifier =
                                    Modifier
                                        .padding(8.dp)
                                        .align(Alignment.TopEnd),
                            ) {
                                Icon(Icons.Filled.ExpandCircleDown, "Switch Account")
                            }
                        } else {
                            IconButton(
                                onClick = {
                                    // add account
                                },
                                modifier =
                                    Modifier
                                        .padding(8.dp)
                                        .align(Alignment.TopEnd),
                            ) {
                                Icon(Icons.Filled.AddCircle, "Add Account")
                            }
                        }
                    }

                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                    ) {
                        BorderedCircularAvatar(
                            profile?.avatar,
                        )

                        profile?.displayName?.let {
                            Text(
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                text = it,
                            )
                        }

                        profile?.handle?.let {
                            Text(
                                style = MaterialTheme.typography.titleMedium,
                                text = "@$it",
                            )
                        }

                        Text(
                            style = MaterialTheme.typography.bodyMedium,
                            text = "${profile?.followersCount} followers • ${profile?.followsCount} following",
                        )

                        HorizontalDivider(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                        )

                        items.forEachIndexed { index, item ->
                            NavigationDrawerItem(
                                label = { Text(item.title) },
                                selected = currentRoute.value == item.route,
                                onClick = {
                                    selectedItemIndex = index
                                    scope.launch {
                                        onNavigate(item.route)
                                        drawerState.close()
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector =
                                            if (index == selectedItemIndex) {
                                                item.selectedIcon
                                            } else {
                                                item.unselectedIcon
                                            },
                                        contentDescription = item.title,
                                    )
                                },
                                badge = {
                                    item.badgeCount?.let {
                                        Text(text = item.badgeCount.toString())
                                    }
                                },
                                modifier =
                                    Modifier
                                        .padding(NavigationDrawerItemDefaults.ItemPadding),
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))
                    }

                    IconButton(
                        modifier = Modifier.padding(16.dp).align(Alignment.BottomEnd),
                        onClick = {
                            scope.launch {
                                onNavigate(NavigationRoutes.Authenticated.Settings.route)
                                drawerState.close()
                            }
                        },
                    ) {
                        Icon(Icons.Filled.Settings, "Settings")
                    }
                }
            }
        },
        gesturesEnabled = true,
        content = content,
    )
}
