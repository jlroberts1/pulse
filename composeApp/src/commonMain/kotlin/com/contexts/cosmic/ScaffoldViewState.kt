package com.contexts.cosmic

import androidx.compose.runtime.Composable

data class ScaffoldViewState(
    val topAppBarTitle: String = "Cosmic",
    val topBarActions: @Composable () -> Unit = {},
    val showTopAppBar: Boolean = true,
    val showFab: Boolean = true,
)
