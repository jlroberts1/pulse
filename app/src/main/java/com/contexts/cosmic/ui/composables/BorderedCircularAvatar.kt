/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.ui.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun BorderedCircularAvatar(
    imageUri: String? = null,
    size: Dp? = null,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = imageUri,
        fallback = rememberVectorPainter(Icons.Default.Person),
        placeholder = rememberVectorPainter(Icons.Default.Person),
        contentDescription = "Profile avatar",
        modifier =
            modifier.size(size ?: 48.dp)
                .clip(CircleShape)
                .border(
                    2.dp,
                    MaterialTheme.colorScheme.surface,
                    CircleShape,
                ),
        contentScale = ContentScale.Crop,
    )
}
