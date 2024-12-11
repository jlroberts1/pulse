/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.DynamicMaterialThemeState

private val LocalAppDimens =
    staticCompositionLocalOf {
        normalDimensions
    }

@Composable
fun ProvideDimens(
    dimensions: Dimensions,
    content: @Composable () -> Unit,
) {
    val dimensionsSet = remember { dimensions }
    CompositionLocalProvider(LocalAppDimens provides dimensionsSet, content = content)
}

@Composable
fun CosmicTheme(
    state: DynamicMaterialThemeState,
    content: @Composable () -> Unit,
) {
    val dimensions = normalDimensions

    ProvideDimens(dimensions = dimensions) {
        DynamicMaterialTheme(
            state = state,
            animate = true,
            typography = Typography,
            content = content,
        )
    }
}
