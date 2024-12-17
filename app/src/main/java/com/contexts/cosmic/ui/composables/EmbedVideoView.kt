/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil3.compose.AsyncImage
import kotlinx.coroutines.flow.StateFlow
import sh.christian.ozone.api.Uri

@Composable
fun EmbedVideoView(
    thumbnail: Uri?,
    playlist: Uri,
    onClick: (String) -> Unit,
    player: ExoPlayer?,
    playerState: StateFlow<Boolean>,
) {
    val noPlayersAvailable by playerState.collectAsStateWithLifecycle()
    if (player == null || noPlayersAvailable) {
        AsyncImage(
            model = thumbnail,
            contentDescription = null,
            modifier = Modifier.clickable { onClick(playlist.uri) },
        )
        return
    }
    Surface(
        onClick = { onClick(playlist.uri) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        border =
            BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant,
            ),
    ) {
        AndroidView(
            modifier =
                Modifier
                    .height(200.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp)),
            factory = { context ->
                PlayerView(context).apply {
                    this.player = player
                    useController = false
                }
            },
            update = { view ->
                view.player = player
            },
        )
    }
}
