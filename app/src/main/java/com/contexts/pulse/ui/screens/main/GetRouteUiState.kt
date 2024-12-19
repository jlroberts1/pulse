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
import androidx.compose.material.icons.sharp.ChatBubbleOutline
import androidx.navigation.NavController

fun getRouteUiState(
    route: String,
    navController: NavController,
): RouteUiState {
    return when (route) {
        NavigationRoutes.Authenticated.Home.route ->
            RouteUiState(
                showFab = true,
                fabIcon = Icons.Sharp.Add,
                fabDesc = "Add new post",
                fabAction = {
                    navController.navigate(NavigationRoutes.Authenticated.AddPost.route)
                },
                showBottomBar = true,
            )
        NavigationRoutes.Authenticated.Profile.route ->
            RouteUiState(
                showTopAppBar = false,
                showBottomBar = true,
            )
        NavigationRoutes.Authenticated.Chat.route ->
            RouteUiState(
                showFab = true,
                fabIcon = Icons.Sharp.ChatBubbleOutline,
                fabDesc = "Start new chat",
                fabAction = {
                    // navigate to start chat
                },
                showBottomBar = true,
            )
        NavigationRoutes.Authenticated.Notifications.route ->
            RouteUiState(
                showBottomBar = true,
            )
        NavigationRoutes.Authenticated.Search.route ->
            RouteUiState(
                showBottomBar = true,
            )
        NavigationRoutes.Authenticated.AddPost.route ->
            RouteUiState(
                topAppBarTitle = "Add new post",
            )
        NavigationRoutes.Unauthenticated.Login.route ->
            RouteUiState(
                showTopAppBar = false,
            )
        else -> RouteUiState()
    }
}
