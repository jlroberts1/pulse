/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.screens.main

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.contexts.pulse.ui.screens.addpost.AddPostScreen
import com.contexts.pulse.ui.screens.chat.ChatScreen
import com.contexts.pulse.ui.screens.home.HomeScreen
import com.contexts.pulse.ui.screens.login.LoginScreen
import com.contexts.pulse.ui.screens.notifications.NotificationsScreen
import com.contexts.pulse.ui.screens.postview.PostViewScreen
import com.contexts.pulse.ui.screens.profile.ProfileScreen
import com.contexts.pulse.ui.screens.search.SearchScreen
import com.contexts.pulse.ui.screens.settings.SettingsScreen

@Composable
fun RootNav(
    navController: NavHostController,
    onMediaOpen: (String) -> Unit,
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.Authenticated.NavigationRoute.route,
        modifier = modifier,
    ) {
        authenticatedGraph(navController, onMediaOpen, drawerState)
        unauthenticatedGraph(navController)
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

        data object ViewProfile : Authenticated("viewprofile/{userDid}") {
            const val ARG_USER_DID = "userDid"

            fun createRoute(userDid: String?) = "viewprofile/$userDid"
        }

        data object Profile : Authenticated("profile")

        data object Settings : Authenticated("settings")

        data object AddPost : Authenticated("add_post/{replyPost}") {
            const val ARG_REPLY_POST = "replyPost"

            fun createRoute(replyPost: String?) = "add_post/$replyPost"
        }

        data object PostView : Authenticated("post_view/{postId}") {
            const val ARG_POST_ID = "postId"

            fun createRoute(postId: String) = "post_view/$postId"
        }
    }
}

fun NavGraphBuilder.unauthenticatedGraph(navController: NavController) {
    navigation(
        route = NavigationRoutes.Unauthenticated.NavigationRoute.route,
        startDestination = NavigationRoutes.Unauthenticated.Login.route,
    ) {
        composable(route = NavigationRoutes.Unauthenticated.Login.route) {
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
    onMediaOpen: (String) -> Unit,
    drawerState: DrawerState,
) {
    navigation(
        route = NavigationRoutes.Authenticated.NavigationRoute.route,
        startDestination = NavigationRoutes.Authenticated.Home.route,
    ) {
        val topLevelTransitionSpec = tween<Float>(300)

        composable(
            route = NavigationRoutes.Authenticated.Home.route,
            enterTransition = { fadeIn(topLevelTransitionSpec) },
            exitTransition = { fadeOut(topLevelTransitionSpec) },
        ) {
            HomeScreen(
                navController = navController,
                onMediaOpen = { onMediaOpen(it) },
                drawerState = drawerState,
            )
        }
        composable(
            route = NavigationRoutes.Authenticated.Search.route,
            enterTransition = { fadeIn(topLevelTransitionSpec) },
            exitTransition = { fadeOut(topLevelTransitionSpec) },
        ) { SearchScreen() }
        composable(
            route = NavigationRoutes.Authenticated.Chat.route,
            enterTransition = { fadeIn(topLevelTransitionSpec) },
            exitTransition = { fadeOut(topLevelTransitionSpec) },
        ) {
            ChatScreen(navController = navController, drawerState = drawerState)
        }
        composable(
            route = NavigationRoutes.Authenticated.Notifications.route,
            enterTransition = { fadeIn(topLevelTransitionSpec) },
            exitTransition = { fadeOut(topLevelTransitionSpec) },
        ) {
            NotificationsScreen(navController = navController, drawerState = drawerState)
        }
        composable(
            route = NavigationRoutes.Authenticated.Profile.route,
            enterTransition = { fadeIn(topLevelTransitionSpec) },
            exitTransition = { fadeOut(topLevelTransitionSpec) },
        ) {
            ProfileScreen(navController = navController, drawerState = drawerState, onMediaOpen = { onMediaOpen(it) })
        }
        composable(
            route = NavigationRoutes.Authenticated.ViewProfile.route,
            arguments =
                listOf(
                    navArgument(NavigationRoutes.Authenticated.ViewProfile.ARG_USER_DID) {
                        type = NavType.StringType
                        nullable = true
                    },
                ),
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300),
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300),
                )
            },
        ) { backStackEntry ->
            val userDid =
                backStackEntry.arguments?.getString(NavigationRoutes.Authenticated.ViewProfile.ARG_USER_DID)
            ProfileScreen(
                userDid = userDid,
                navController = navController,
                drawerState = drawerState,
                onMediaOpen = { onMediaOpen(it) },
            )
        }
        composable(
            route = NavigationRoutes.Authenticated.Settings.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300),
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300),
                )
            },
        ) {
            SettingsScreen(
                navController = navController,
                drawerState = drawerState,
            )
        }
        composable(
            route = NavigationRoutes.Authenticated.AddPost.route,
            arguments =
                listOf(
                    navArgument(NavigationRoutes.Authenticated.AddPost.ARG_REPLY_POST) {
                        type = NavType.StringType
                        nullable = true
                    },
                ),
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300),
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300),
                )
            },
        ) { backStackEntry ->
            val replyPost =
                backStackEntry.arguments?.getString(NavigationRoutes.Authenticated.AddPost.ARG_REPLY_POST)
            AddPostScreen(
                replyPost = replyPost,
                navController = navController,
                drawerState = drawerState,
                onPostSent = { navController.navigateUp() },
            )
        }
        composable(
            route = NavigationRoutes.Authenticated.PostView.route,
            arguments =
                listOf(
                    navArgument(NavigationRoutes.Authenticated.PostView.ARG_POST_ID) {
                        type = NavType.StringType
                    },
                ),
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(300),
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(300),
                )
            },
        ) { backStackEntry ->
            val postId =
                backStackEntry.arguments?.getString(NavigationRoutes.Authenticated.PostView.ARG_POST_ID)
                    ?: return@composable

            PostViewScreen(
                postId = postId,
                navController = navController,
                drawerState = drawerState,
            )
        }
    }
}
