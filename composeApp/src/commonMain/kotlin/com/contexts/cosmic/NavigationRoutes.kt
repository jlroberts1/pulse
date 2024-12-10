package com.contexts.cosmic

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
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
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.Authenticated.NavigationRoute.route,
        modifier = modifier,
    ) {
        authenticatedGraph(navController)
        unauthenticateddGraph(navController)
    }
}

@Composable
fun UnauthenticatedNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.Unauthenticated.NavigationRoute.route,
        modifier = modifier,
    ) {
        unauthenticateddGraph(navController)
        authenticatedGraph(navController)
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

fun NavGraphBuilder.unauthenticateddGraph(navController: NavController) {
    navigation(
        route = NavigationRoutes.Unauthenticated.NavigationRoute.route,
        startDestination = NavigationRoutes.Unauthenticated.Login.route,
    ) {
        composable(route = NavigationRoutes.Unauthenticated.Login.route) {
            LoginScreen()
        }
    }
}

fun NavGraphBuilder.authenticatedGraph(navController: NavController) {
    navigation(
        route = NavigationRoutes.Authenticated.NavigationRoute.route,
        startDestination = NavigationRoutes.Authenticated.Home.route,
    ) {
        composable(route = NavigationRoutes.Authenticated.Home.route) {
            HomeScreen()
        }
        composable(route = NavigationRoutes.Authenticated.Search.route) {
            SearchScreen()
        }
        composable(route = NavigationRoutes.Authenticated.Messages.route) {
            MessageScreen()
        }
        composable(route = NavigationRoutes.Authenticated.Notifications.route) {
            NotificationsScreen()
        }
        composable(route = NavigationRoutes.Authenticated.Profile.route) {
            ProfileScreen()
        }
        composable(route = NavigationRoutes.Authenticated.Settings.route) {
            SettingsScreen()
        }
    }
}
