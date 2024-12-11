/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

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
