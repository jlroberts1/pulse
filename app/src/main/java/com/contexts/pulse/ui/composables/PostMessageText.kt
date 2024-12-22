/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PostMessageText(
    text: String? = "",
    onClick: () -> Unit,
) {
    AutoLinkText(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
        text = text ?: "",
        onClick = { onClick() },
    )
}
