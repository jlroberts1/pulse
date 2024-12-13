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
fun AuthenticatedNavigation(
    navController: NavHostController,
    updateScaffoldViewState: (ScaffoldViewState) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.Authenticated.NavigationRoute.route,
        modifier = modifier,
    ) {
        authenticatedGraph(navController, updateScaffoldViewState)
    }
}

@Composable
fun UnauthenticatedNavigation(
    navController: NavHostController,
    updateScaffoldViewState: (ScaffoldViewState) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.Unauthenticated.NavigationRoute.route,
        modifier = modifier,
    ) {
        unauthenticateddGraph(navController, updateScaffoldViewState)
    }
}

sealed class NavigationRoutes {
    sealed class Unauthenticated(val route: String) : NavigationRoutes() {
        data object NavigationRoute : Unauthenticated("unauthenticated")

        data object Login : Unauthenticated("login")
    }

    sealed class Authenticated(val route: String) : NavigationRoutes() {
        data object NavigationRoute : Authenticated("authenticated")

        data object Home : Authenticated("home")

        data object Search : Authenticated("search")

        data object Messages : Authenticated("messages")

        data object Notifications : Authenticated("notifications")

        data object Profile : Authenticated("profile")

        data object Settings : Authenticated("settings")
    }
}

fun NavGraphBuilder.unauthenticateddGraph(
    navController: NavController,
    updateScaffoldViewState: (ScaffoldViewState) -> Unit,
) {
    navigation(
        route = NavigationRoutes.Unauthenticated.NavigationRoute.route,
        startDestination = NavigationRoutes.Unauthenticated.Login.route,
    ) {
        composable(route = NavigationRoutes.Unauthenticated.Login.route) {
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
        route = NavigationRoutes.Authenticated.NavigationRoute.route,
        startDestination = NavigationRoutes.Authenticated.Home.route,
    ) {
        composable(route = NavigationRoutes.Authenticated.Home.route) {
            updateScaffoldViewState(
                ScaffoldViewState(
                    showTopAppBar = true,
                    showFab = true,
                    topBarActions = {
                        HomeOverflowMenu {
                            when (it) {
                                is HomeOverflowMenuAction.Settings ->
                                    navController.navigate(
                                        NavigationRoutes.Authenticated.Settings.route,
                                    )
                                is HomeOverflowMenuAction.Logout -> Unit
                            }
                        }
                    },
                ),
            )
            HomeScreen()
        }
        composable(route = NavigationRoutes.Authenticated.Search.route) {
            updateScaffoldViewState(
                ScaffoldViewState(
                    showTopAppBar = true,
                    showFab = true,
                ),
            )
            SearchScreen()
        }
        composable(route = NavigationRoutes.Authenticated.Messages.route) {
            updateScaffoldViewState(
                ScaffoldViewState(
                    showTopAppBar = true,
                    showFab = true,
                ),
            )
            MessageScreen()
        }
        composable(route = NavigationRoutes.Authenticated.Notifications.route) {
            updateScaffoldViewState(
                ScaffoldViewState(
                    showTopAppBar = true,
                    showFab = true,
                ),
            )
            NotificationsScreen()
        }
        composable(route = NavigationRoutes.Authenticated.Profile.route) {
            updateScaffoldViewState(
                ScaffoldViewState(
                    showTopAppBar = false,
                    showFab = false,
                ),
            )
            ProfileScreen()
        }
        composable(route = NavigationRoutes.Authenticated.Settings.route) {
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
