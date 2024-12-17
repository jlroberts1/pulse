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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.media3.common.MediaItem
import app.bsky.feed.PostViewEmbedUnion
import com.contexts.cosmic.domain.media.PlayerPoolManager
import org.koin.compose.koinInject

@Composable
fun EmbedView(
    embed: PostViewEmbedUnion,
    onMediaOpen: (String) -> Unit,
    playerPoolManager: PlayerPoolManager = koinInject(),
) {
    when (embed) {
        is PostViewEmbedUnion.RecordWithMediaView -> {
        }

        is PostViewEmbedUnion.RecordView -> {
            EmbedRecordView(
                record = embed.value.record,
            )
        }

        is PostViewEmbedUnion.VideoView -> {
            val player =
                remember(embed.value.playlist) { playerPoolManager.getPlayer() }?.apply {
                    setMediaItem(MediaItem.fromUri(embed.value.playlist.uri))
                    prepare()
                    playWhenReady = true
                }
            DisposableEffect(player) {
                onDispose {
                    player?.let { playerPoolManager.releasePlayer(it) }
                }
            }
            EmbedVideoView(
                thumbnail = embed.value.thumbnail,
                playlist = embed.value.playlist,
                onClick = { onMediaOpen(it) },
                aspectRatio = embed.value.aspectRatio,
                player = player,
                playerState = playerPoolManager.noPlayersAvailable,
            )
        }

        is PostViewEmbedUnion.ExternalView -> {
            EmbedExternalView(
                uri = embed.value.external.uri,
                thumb = embed.value.external.thumb,
                title = embed.value.external.title,
                description = embed.value.external.description,
                onMediaOpen = { onMediaOpen(it) },
            )
        }

        is PostViewEmbedUnion.ImagesView -> {
            EmbedImageView(
                embed.value.images,
                onClick = { onMediaOpen(it) },
            )
        }

        is PostViewEmbedUnion.Unknown -> {
        }
    }
}
