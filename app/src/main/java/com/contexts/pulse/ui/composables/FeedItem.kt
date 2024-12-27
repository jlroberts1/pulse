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
import app.bsky.feed.FeedViewPost
import app.bsky.feed.PostViewEmbedUnion
import com.contexts.pulse.domain.media.PlayerPoolManager
import com.contexts.pulse.extensions.getPostText
import org.koin.compose.koinInject

@Composable
fun FeedItem(
    post: FeedViewPost,
    onPostClick: (String) -> Unit,
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
                .clickable {
                    onPostClick(post.post.uri.atUri)
                }
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
                avatar = post.post.author.avatar?.uri,
                displayName = post.post.author.displayName,
                handle = post.post.author.handle.handle,
                indexedAt = post.post.indexedAt,
            )
            PostMessageText(
                text = post.post.getPostText(),
                onClick = { onPostClick(post.post.uri.atUri) },
            )

            post.post.embed?.let { embed ->
                when (embed) {
                    is PostViewEmbedUnion.ExternalView -> {
                        with(embed.value.external) {
                            EmbedExternalView(
                                uri = uri,
                                thumb = thumb,
                                onMediaOpen = { onMediaOpen(it) },
                                title = title,
                            )
                        }
                    }

                    is PostViewEmbedUnion.ImagesView -> {
                        EmbedImagesViewImage(
                            embed.value.images,
                            onMediaOpen = { onMediaOpen(it) },
                        )
                    }

                    is PostViewEmbedUnion.RecordView -> {
                        EmbedRecordView(embed.value.record)
                    }

                    is PostViewEmbedUnion.RecordWithMediaView -> Unit

                    is PostViewEmbedUnion.VideoView -> {
                        with(embed.value) {
                            val player =
                                remember(playlist) { playerPoolManager.getPlayer() }?.apply {
                                    setMediaItem(MediaItem.fromUri(playlist.uri))
                                    prepare()
                                    playWhenReady = true
                                }
                            DisposableEffect(player) {
                                onDispose {
                                    player?.let { playerPoolManager.releasePlayer(it) }
                                }
                            }
                            EmbedVideoView(
                                thumbnail = thumbnail,
                                playlist = playlist,
                                aspectRatio = aspectRatio,
                                onMediaOpen = { onMediaOpen(it) },
                                player = player,
                                playerState = playerPoolManager.noPlayersAvailable,
                            )
                        }
                    }

                    is PostViewEmbedUnion.Unknown -> Unit
                }
            }

            FeedItemInteractions(
                replyCount = post.post.replyCount,
                repostCount = post.post.repostCount,
                likeCount = post.post.likeCount,
            )
        }
    }
}
