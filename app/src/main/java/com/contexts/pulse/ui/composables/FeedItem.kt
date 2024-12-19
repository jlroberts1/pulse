/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import com.contexts.pulse.data.local.database.entities.FeedPostEntity
import com.contexts.pulse.domain.media.PlayerPoolManager
import org.koin.compose.koinInject

@Composable
fun FeedItem(
    post: FeedPostEntity,
    onReplyClick: () -> Unit,
    onRepostClick: () -> Unit,
    onLikeClick: () -> Unit,
    onMenuClick: () -> Unit,
    playerPoolManager: PlayerPoolManager = koinInject(),
    onMediaOpen: (String) -> Unit,
) {
    ElevatedCard(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, start = 8.dp, end = 8.dp),
        ) {
            PostHeader(
                avatar = post.authorAvatar,
                displayName = post.authorDisplayName,
                handle = post.authorHandle,
                indexedAt = post.indexedAt,
            )
            PostMessageText(post.content)
            post.embedExternal?.let {
                EmbedExternalView(
                    uri = it.external.uri,
                    thumb = it.external.thumb,
                    title = it.external.title,
                    description = it.external.description,
                    onMediaOpen = { media -> onMediaOpen(media) },
                )
            }

            post.embedVideo?.let {
                val player =
                    remember(it.playlist) { playerPoolManager.getPlayer() }?.apply {
                        setMediaItem(MediaItem.fromUri(it.playlist.uri))
                        prepare()
                        playWhenReady = true
                    }
                DisposableEffect(player) {
                    onDispose {
                        player?.let { playerPoolManager.releasePlayer(it) }
                    }
                }
                EmbedVideoView(
                    thumbnail = it.thumbnail,
                    playlist = it.playlist,
                    aspectRatio = it.aspectRatio,
                    onClick = { media -> onMediaOpen(media) },
                    player = player,
                    playerState = playerPoolManager.noPlayersAvailable,
                )
            }

            post.embedImages?.let {
                EmbedImageView(images = it, onClick = { media -> onMediaOpen(media) })
            }

            post.embedRecord?.let {
                EmbedRecordView(record = it.record)
            }

            post.embedRecordWithMedia?.let {
            }

            FeedItemInteractions(
                replyCount = post.replyCount,
                repostCount = post.replyCount,
                likeCount = post.likeCount,
            )
        }
    }
}
