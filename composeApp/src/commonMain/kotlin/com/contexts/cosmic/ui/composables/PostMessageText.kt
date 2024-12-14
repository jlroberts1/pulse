/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.ui.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import sh.calvin.autolinktext.AutoLinkText

@Composable
fun PostMessageText(text: String? = "") {
    AutoLinkText(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
        text = text ?: "",
        style =
            MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
    )
}
