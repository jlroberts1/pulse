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
import com.contexts.pulse.domain.model.EmbedImage

@Composable
fun EmbedImagesViewImage(
    images: List<EmbedImage>,
    onMediaOpen: (String) -> Unit,
) {
    if (images.size == 1) {
        SingleImagesViewHolder(
            images.first(),
            onClick = { onMediaOpen(it) },
        )
    } else {
        MultiImagesViewHolder(
            images,
            onClick = { onMediaOpen(it) },
        )
    }
}
