package com.contexts.cosmic

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable

data class ScaffoldViewState(
    val topAppBarTitle: String = "Cosmic",
    val fabIcon: @Composable () -> Unit = {
        Icon(Icons.Sharp.Add, "Add")
    },
    val topBarActions: @Composable () -> Unit = {},
    val fabOnClick: () -> Unit = {},
    val showTopAppBar: Boolean = true,
    val showFab: Boolean = true,
)
