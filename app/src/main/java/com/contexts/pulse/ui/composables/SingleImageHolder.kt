/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import app.bsky.embed.ImagesViewImage
import coil3.compose.AsyncImage
import com.contexts.pulse.extensions.toFloat

@Composable
fun SingleImageHolder(
    image: ImagesViewImage,
    onClick: (String) -> Unit,
) {
    AsyncImage(
        model = image.fullsize.uri,
        contentDescription = image.alt,
        modifier =
            Modifier
                .padding(horizontal = 4.dp)
                .fillMaxWidth()
                .aspectRatio(image.aspectRatio.toFloat())
                .clip(RoundedCornerShape(8.dp))
                .clickable { onClick(image.fullsize.uri) },
        contentScale = ContentScale.Crop,
    )
}
