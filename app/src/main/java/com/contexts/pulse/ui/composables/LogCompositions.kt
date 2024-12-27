/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.currentCompositionLocalContext
import logcat.logcat

@Composable
fun LogCompositions(tag: String = "Recomposition") {
    val context = currentCompositionLocalContext
    SideEffect {
        logcat("LogCompositions") { "Composition: $context" }
    }
}
