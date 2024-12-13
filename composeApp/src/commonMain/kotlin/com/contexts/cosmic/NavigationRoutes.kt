/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.contexts.cosmic.ui.screens.home.HomeOverflowMenu
import com.contexts.cosmic.ui.screens.home.HomeOverflowMenuAction
import com.contexts.cosmic.ui.screens.home.HomeScreen
import com.contexts.cosmic.ui.screens.login.LoginScreen
import com.contexts.cosmic.ui.screens.messages.MessageScreen
import com.contexts.cosmic.ui.screens.notifications.NotificationsScreen
import com.contexts.cosmic.ui.screens.profile.ProfileScreen
import com.contexts.cosmic.ui.screens.search.SearchScreen
import com.contexts.cosmic.ui.screens.settings.SettingsScreen

@Composable
fun RootNav(
    navController: NavHostController,
    updateScaffoldViewState: (ScaffoldViewState) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.Unauthenticated.route,
        modifier = modifier,
    ) {
        authenticatedGraph(navController, updateScaffoldViewState)
        unauthenticatedGraph(navController, updateScaffoldViewState)
    }
}

sealed class NavigationRoutes(val route: String) {
    data object Unauthenticated : NavigationRoutes("unauthenticated")

    data object Authenticated : NavigationRoutes("authenticated")

    data object Login : NavigationRoutes("login")

    data object Home : NavigationRoutes("home")

    data object Search : NavigationRoutes("search")

    data object Messages : NavigationRoutes("messages")

    data object Notifications : NavigationRoutes("notifications")

    data object Profile : NavigationRoutes("profile")

    data object Settings : NavigationRoutes("settings")
}

fun NavGraphBuilder.unauthenticatedGraph(
    navController: NavController,
    updateScaffoldViewState: (ScaffoldViewState) -> Unit,
) {
    navigation(
        route = NavigationRoutes.Unauthenticated.route,
        startDestination = NavigationRoutes.Login.route,
    ) {
        composable(route = NavigationRoutes.Login.route) {
            updateScaffoldViewState(
                ScaffoldViewState(
                    showTopAppBar = false,
                    showFab = false,
                ),
            )
            LoginScreen()
        }
    }
}

fun NavGraphBuilder.authenticatedGraph(
    navController: NavController,
    updateScaffoldViewState: (ScaffoldViewState) -> Unit,
) {
    navigation(
        route = NavigationRoutes.Authenticated.route,
        startDestination = NavigationRoutes.Home.route,
    ) {
        composable(route = NavigationRoutes.Home.route) {
            updateScaffoldViewState(
                ScaffoldViewState(
                    showTopAppBar = true,
                    showFab = true,
                    topBarActions = {
                        HomeOverflowMenu {
                            when (it) {
                                is HomeOverflowMenuAction.Settings ->
                                    navController.navigate(
                                        NavigationRoutes.Settings.route,
                                    )
                                is HomeOverflowMenuAction.Logout -> Unit
                            }
                        }
                    },
                ),
            )
            HomeScreen()
        }
        composable(route = NavigationRoutes.Search.route) {
            updateScaffoldViewState(
                ScaffoldViewState(
                    showTopAppBar = true,
                    showFab = true,
                ),
            )
            SearchScreen()
        }
        composable(route = NavigationRoutes.Messages.route) {
            updateScaffoldViewState(
                ScaffoldViewState(
                    showTopAppBar = true,
                    showFab = true,
                ),
            )
            MessageScreen()
        }
        composable(route = NavigationRoutes.Notifications.route) {
            updateScaffoldViewState(
                ScaffoldViewState(
                    showTopAppBar = true,
                    showFab = true,
                ),
            )
            NotificationsScreen()
        }
        composable(route = NavigationRoutes.Profile.route) {
            updateScaffoldViewState(
                ScaffoldViewState(
                    showTopAppBar = false,
                    showFab = false,
                ),
            )
            ProfileScreen()
        }
        composable(route = NavigationRoutes.Settings.route) {
            updateScaffoldViewState(
                ScaffoldViewState(
                    showTopAppBar = true,
                    topAppBarTitle = "Settings",
                    showFab = false,
                ),
            )
            SettingsScreen()
        }
    }
}
