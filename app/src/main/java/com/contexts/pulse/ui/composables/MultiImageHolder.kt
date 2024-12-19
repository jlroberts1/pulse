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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import app.bsky.embed.ImagesViewImage
import coil3.compose.AsyncImage

@Composable
fun MultiImageHolder(
    images: List<ImagesViewImage>,
    onClick: (String) -> Unit,
) {
    HorizontalPager(
        state = rememberPagerState(pageCount = { images.size }),
        contentPadding = PaddingValues(horizontal = 4.dp),
        modifier =
            Modifier
                .height(200.dp)
                .fillMaxWidth(),
    ) { page ->
        AsyncImage(
            model = images[page].fullsize.uri,
            contentDescription = images[page].alt,
            modifier =
                Modifier
                    .padding(end = 4.dp)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onClick(images[page].fullsize.uri) },
            contentScale = ContentScale.Crop,
        )
    }
}
