/*
 * Copyright (c) 2025. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun BadgedImage(
    modifier: Modifier = Modifier,
    image: String,
    contentDescription: String,
    onClick: () -> Unit = {},
    icon: @Composable () -> Unit,
) {
    Box(
        modifier = modifier,
    ) {
        AsyncImage(
            model = image,
            contentDescription = contentDescription,
            modifier =
                Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
        )

        IconButton(
            onClick = { onClick() },
            modifier =
                Modifier
                    .size(18.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 8.dp, y = (-8).dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = CircleShape,
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        shape = CircleShape,
                    ),
        ) {
            icon()
        }
    }
}
