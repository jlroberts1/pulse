package com.contexts.cosmic.ui.composables

import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import com.contexts.cosmic.ui.components.SnackbarDelegate

@Composable
fun SnackbarHost(
    snackbarHostState: SnackbarHostState,
    snackbarDelegate: SnackbarDelegate,
) {
    androidx.compose.material3.SnackbarHost(hostState = snackbarHostState) {
        val backgroundColor = snackbarDelegate.snackbarBackgroundColor
        Snackbar(
            snackbarData = it,
            containerColor = backgroundColor,
        )
    }
}
