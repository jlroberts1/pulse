package com.contexts.cosmic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.contexts.cosmic.domain.repository.PreferencesRepository
import com.contexts.cosmic.ui.screens.login.LoginScreen
import com.contexts.cosmic.ui.screens.profile.ProfileScreen
import org.koin.compose.koinInject

@Composable
fun MainAppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    preferencesRepository: PreferencesRepository = koinInject(),
) {
    var appState by remember {
        mutableStateOf<AppState>(AppState.Loading)
    }

    LaunchedEffect(Unit) {
        preferencesRepository.getTokens().collect { tokens ->
            appState  =
                if (tokens?.accessToken.isNullOrEmpty()) {
                    AppState.Unauthenticated
                } else {
                    AppState.Authenticated
                }
        }
    }
    when (appState) {
        is AppState.Loading -> {
            SplashScreen()
        }

        is AppState.Authenticated -> {
            NavHost(
                modifier = modifier,
                navController = navController,
                startDestination = NavigationRoutes.Authenticated.NavigationRoute.route
            ) {
                authenticatedGraph(navController)
                unauthenticateddGraph(navController)
            }
        }

        is AppState.Unauthenticated -> {
            NavHost(
                modifier = modifier,
                navController = navController,
                startDestination = NavigationRoutes.Unauthenticated.NavigationRoute.route
            ) {
                unauthenticateddGraph(navController)
                authenticatedGraph(navController)
            }
        }
    }
}

sealed class NavigationRoutes {
    sealed class Unauthenticated(val route: String) : NavigationRoutes() {
        data object NavigationRoute : Unauthenticated("unauthenticated")

        data object Login : Unauthenticated("login")
    }

    sealed class Authenticated(val route: String) : NavigationRoutes() {
        data object NavigationRoute : Authenticated("authenticated")

        data object Profile : Authenticated("profile")
    }
}

fun NavGraphBuilder.unauthenticateddGraph(
    navController: NavController
) {
    navigation(
        route = NavigationRoutes.Unauthenticated.NavigationRoute.route,
        startDestination = NavigationRoutes.Unauthenticated.Login.route,
    ) {
        composable(route = NavigationRoutes.Unauthenticated.Login.route) {
            LoginScreen(
                onNavigateToAuthenticatedRoute = {
                    navController.navigate(NavigationRoutes.Authenticated.Profile.route) {
                        popUpTo(route = NavigationRoutes.Unauthenticated.NavigationRoute.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}

fun NavGraphBuilder.authenticatedGraph(
    navController: NavController
) {
    navigation(
        route = NavigationRoutes.Authenticated.NavigationRoute.route,
        startDestination = NavigationRoutes.Authenticated.Profile.route,
    ) {
        composable(route = NavigationRoutes.Authenticated.Profile.route) {
            ProfileScreen()
        }
    }
}

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}