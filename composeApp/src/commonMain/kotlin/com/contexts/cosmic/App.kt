package com.contexts.cosmic

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.contexts.cosmic.domain.model.NavigationIcon
import com.contexts.cosmic.ui.components.SnackbarDelegate
import com.contexts.cosmic.ui.composables.SnackbarHost
import com.contexts.cosmic.ui.composables.Splash
import com.contexts.cosmic.ui.composables.TopBar
import com.contexts.cosmic.ui.theme.CosmicTheme
import com.materialkolor.rememberDynamicMaterialThemeState
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App(
    viewModel: MainViewModel = koinViewModel(),
    snackbarDelegate: SnackbarDelegate = koinInject(),
) {
    val isDarkTheme = isSystemInDarkTheme()
    val isAmoled by rememberSaveable { mutableStateOf(false) }
    val color = MaterialTheme.colorScheme.primary.toArgb()
    val seedColor by rememberSaveable { mutableStateOf(color) }
    val snackbarHostState = remember { SnackbarHostState() }
    val authState by viewModel.authState.collectAsState()

    snackbarDelegate.apply {
        this.snackbarHostState = snackbarHostState
        coroutineScope = rememberCoroutineScope()
    }

    val state =
        rememberDynamicMaterialThemeState(
            seedColor = Color(seedColor),
            isDark = isDarkTheme,
            isAmoled = isAmoled,
        )

    val navController = rememberNavController()

    val showBottomBar =
        navController
            .currentBackStackEntryAsState()
            .value?.destination?.route in bottomNavDestinations.map { it.route }

    CosmicTheme(state) {
        KoinContext {
            Scaffold(
                topBar = { TopBar(navController) },
                snackbarHost = { SnackbarHost(snackbarHostState, snackbarDelegate) },
                bottomBar = {
                    AnimatedVisibility(
                        visible = showBottomBar,
                    ) {
                        NavigationBar {
                            bottomNavDestinations.forEach { screen ->
                                val selected = navController.currentBackStackEntry?.destination?.route == screen.route
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
                                        Icon(icon.image, contentDescription = icon.contentDescription)
                                    },
                                )
                            }
                        }
                    }
                },
            ) { innerPadding ->
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    AnimatedContent(
                        targetState = authState,
                    ) { state ->
                        when (state) {
                            AuthenticationState.Loading -> {
                                if (getPlatform().isIOS()) {
                                    Splash()
                                }
                            }
                            AuthenticationState.Authenticated ->
                                AuthenticatedNavigation(
                                    navController,
                                    modifier = Modifier.padding(innerPadding),
                                )

                            AuthenticationState.Unauthenticated ->
                                UnauthenticatedNavigation(
                                    navController,
                                    modifier = Modifier.padding(innerPadding),
                                )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun getIconForScreen(screen: NavigationRoutes): NavigationIcon {
    return when (screen) {
        NavigationRoutes.Authenticated.Home -> NavigationIcon(Icons.Default.Home, "Home")
        NavigationRoutes.Authenticated.Search -> NavigationIcon(Icons.Default.Search, "Search")
        NavigationRoutes.Authenticated.Messages -> NavigationIcon(Icons.AutoMirrored.Filled.Message, "Messages")
        NavigationRoutes.Authenticated.Notifications -> NavigationIcon(Icons.Default.Notifications, "Notifications")
        NavigationRoutes.Authenticated.Profile -> NavigationIcon(Icons.Default.Person, "Profile")
        else -> NavigationIcon(Icons.Default.Home, "Home")
    }
}

val bottomNavDestinations =
    listOf(
        NavigationRoutes.Authenticated.Home,
        NavigationRoutes.Authenticated.Search,
        NavigationRoutes.Authenticated.Messages,
        NavigationRoutes.Authenticated.Notifications,
        NavigationRoutes.Authenticated.Profile,
    )
