/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.composables

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.ui.PlayerView
import app.bsky.embed.AspectRatio
import coil3.compose.AsyncImage
import com.contexts.pulse.domain.media.PlayerPoolManager
import com.contexts.pulse.extensions.toFloat
import org.koin.compose.koinInject
import sh.christian.ozone.api.Uri

@Composable
fun EmbedVideoView(
    thumbnail: Uri?,
    playlist: Uri,
    aspectRatio: AspectRatio?,
    onMediaOpen: (String) -> Unit,
    playerPoolManager: PlayerPoolManager = koinInject(),
) {
    val player =
        remember(playlist) { playerPoolManager.getPlayer() }?.apply {
            setMediaItem(MediaItem.fromUri(playlist.uri))
            prepare()
            playWhenReady = true
        }

    val noPlayersAvailable by playerPoolManager.noPlayersAvailable.collectAsStateWithLifecycle()
    DisposableEffect(player) {
        onDispose {
            player?.let { playerPoolManager.releasePlayer(it) }
        }
    }
    var playerPlaying by remember { mutableStateOf(false) }
    player?.addListener(
        object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                playerPlaying = isPlaying
            }
        },
    )
    Surface(
        onClick = { onMediaOpen(playlist.uri) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        border =
            BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant,
            ),
    ) {
        Crossfade(
            label = playlist.uri,
            targetState = player != null && !noPlayersAvailable && playerPlaying,
            animationSpec = tween(300),
        ) { isPlaying ->
            if (!isPlaying) {
                AsyncImage(
                    model = thumbnail?.uri,
                    contentDescription = null,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .aspectRatio(aspectRatio.toFloat())
                            .clickable { onMediaOpen(playlist.uri) },
                )
            } else {
                AndroidView(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .aspectRatio(aspectRatio.toFloat()),
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
    }
}
