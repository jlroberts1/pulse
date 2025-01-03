/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.contexts.pulse.domain.model.Theme
import com.contexts.pulse.ui.composables.ScaffoldedScreen
import com.contexts.pulse.ui.screens.settings.composables.Preference
import com.contexts.pulse.ui.screens.settings.composables.SelectionDialog
import com.contexts.pulse.ui.screens.settings.composables.SelectionItem
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
    navController: NavController,
    drawerState: DrawerState,
) {
    val scrollState = rememberScrollState()
    var showThemeDialog by remember { mutableStateOf(false) }
    val theme = viewModel.theme.collectAsStateWithLifecycle()
    ScaffoldedScreen(
        navController = navController,
        title = "Settings",
        drawerState = drawerState,
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(scrollState),
        ) {
            val currentTheme =
                theme.value.name
                    .lowercase()
                    .capitalize(Locale.current)
            Preference(
                title = "Theme",
                summary = "Currently set as $currentTheme",
                onClicked = {
                    showThemeDialog = true
                },
            )
            SelectionDialog(
                isVisible = showThemeDialog,
                currentSelection = theme.value.name,
                onDismiss = { showThemeDialog = false },
                options =
                    listOf(
                        SelectionItem(
                            "System",
                            Theme.SYSTEM.name,
                        ),
                        SelectionItem(
                            "Light",
                            Theme.LIGHT.name,
                        ),
                        SelectionItem(
                            "Dark",
                            Theme.DARK.name,
                        ),
                    ),
                onSelected = { newTheme ->
                    viewModel.updateTheme(
                        Theme.fromString(newTheme),
                    )
                },
            )
        }
    }
}
