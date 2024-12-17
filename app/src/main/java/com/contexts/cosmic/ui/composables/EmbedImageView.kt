/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.ui.composables

import androidx.compose.runtime.Composable
import app.bsky.embed.ImagesViewImage

@Composable
fun EmbedImageView(
    images: List<ImagesViewImage>,
    onClick: (String) -> Unit,
) {
    if (images.size == 1) {
        SingleImageHolder(
            images.first(),
            onClick = { onClick(it) },
        )
    } else {
        MultiImageHolder(
            images,
            onClick = { onClick(it) },
        )
    }
}
