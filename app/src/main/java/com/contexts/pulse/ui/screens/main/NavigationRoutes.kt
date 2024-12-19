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
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.contexts.pulse.ui.screens.addpost.AddPostScreen
import com.contexts.pulse.ui.screens.chat.ChatScreen
import com.contexts.pulse.ui.screens.home.HomeScreen
import com.contexts.pulse.ui.screens.login.LoginScreen
import com.contexts.pulse.ui.screens.notifications.NotificationsScreen
import com.contexts.pulse.ui.screens.profile.ProfileScreen
import com.contexts.pulse.ui.screens.search.SearchScreen
import com.contexts.pulse.ui.screens.settings.SettingsScreen

@Composable
fun RootNav(
    navController: NavHostController,
    controlsVisibility: Float,
    onMediaOpen: (String) -> Unit,
    updateScaffoldViewState: (ScaffoldViewState) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.Authenticated.NavigationRoute.route,
        modifier = modifier,
    ) {
        authenticatedGraph(navController, controlsVisibility, onMediaOpen, updateScaffoldViewState)
        unauthenticatedGraph(navController, updateScaffoldViewState)
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

        data object Chat : Authenticated("chat")

        data object Notifications : Authenticated("notifications")

        data object Profile : Authenticated("profile")

        data object Settings : Authenticated("settings")

        data object AddPost : Authenticated("add_post")
    }
}

fun NavGraphBuilder.unauthenticatedGraph(
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
            LoginScreen {
                navController.navigate(NavigationRoutes.Authenticated.NavigationRoute.route) {
                    popUpTo(NavigationRoutes.Authenticated.NavigationRoute.route) {
                        inclusive = true
                    }
                }
            }
        }
    }
}

fun NavGraphBuilder.authenticatedGraph(
    navController: NavController,
    controlsVisibility: Float,
    onMediaOpen: (String) -> Unit,
    updateScaffoldViewState: (ScaffoldViewState) -> Unit,
) {
    navigation(
        route = NavigationRoutes.Authenticated.NavigationRoute.route,
        startDestination = NavigationRoutes.Authenticated.Home.route,
    ) {
        composable(route = NavigationRoutes.Authenticated.Home.route) {
            updateScaffoldViewState(
                ScaffoldViewState(
                    showFab = true,
                    showTopAppBar = true,
                    fabAction = {
                        navController.navigate(NavigationRoutes.Authenticated.AddPost.route)
                    },
                ),
            )
            HomeScreen(
                controlsVisibility = controlsVisibility,
                onMediaOpen = { onMediaOpen(it) },
            )
        }
        composable(route = NavigationRoutes.Authenticated.Search.route) {
            updateScaffoldViewState(
                ScaffoldViewState(
                    showFab = false,
                    showTopAppBar = true,
                ),
            )
            SearchScreen()
        }
        composable(route = NavigationRoutes.Authenticated.Chat.route) {
            updateScaffoldViewState(
                ScaffoldViewState(
                    showFab = true,
                    showTopAppBar = true,
                ),
            )
            ChatScreen()
        }
        composable(route = NavigationRoutes.Authenticated.Notifications.route) {
            updateScaffoldViewState(
                ScaffoldViewState(
                    showFab = true,
                    showTopAppBar = true,
                    fabAction = {
                        navController.navigate(NavigationRoutes.Authenticated.AddPost.route)
                    },
                ),
            )
            NotificationsScreen()
        }
        composable(route = NavigationRoutes.Authenticated.Profile.route) {
            updateScaffoldViewState(
                ScaffoldViewState(
                    showFab = true,
                    showTopAppBar = false,
                    fabAction = {
                        navController.navigate(NavigationRoutes.Authenticated.AddPost.route)
                    },
                ),
            )
            ProfileScreen(
                onMediaOpen = { onMediaOpen(it) },
            )
        }
        composable(route = NavigationRoutes.Authenticated.Settings.route) {
            updateScaffoldViewState(
                ScaffoldViewState(
                    showFab = false,
                    showTopAppBar = true,
                ),
            )
            SettingsScreen()
        }
        composable(route = NavigationRoutes.Authenticated.AddPost.route) {
            updateScaffoldViewState(
                ScaffoldViewState(
                    showFab = false,
                    topAppBarTitle = "Add new post",
                    showTopAppBar = true,
                ),
            )
            AddPostScreen()
        }
    }
}
