/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.screens.postview.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import app.bsky.feed.PostView
import app.bsky.feed.PostViewEmbedUnion
import com.contexts.pulse.domain.media.PlayerPoolManager
import com.contexts.pulse.extensions.getPostText
import com.contexts.pulse.ui.composables.EmbedExternalView
import com.contexts.pulse.ui.composables.EmbedImagesViewImage
import com.contexts.pulse.ui.composables.EmbedRecordView
import com.contexts.pulse.ui.composables.EmbedVideoView
import com.contexts.pulse.ui.composables.FeedItemInteractions
import com.contexts.pulse.ui.composables.PostHeader
import com.contexts.pulse.ui.composables.PostMessageText
import io.ktor.http.encodeURLParameter
import org.koin.compose.koinInject

@Composable
fun PostViewItem(
    post: PostView,
    isReply: Boolean = false,
    onPostClick: (String) -> Unit,
    onReplyClick: (String) -> Unit,
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
                    onPostClick(post.uri.atUri)
                }
                .fillMaxWidth(),
    ) {
        Box {
            if (isReply) {
                Box(
                    modifier =
                        Modifier
                            .width(2.dp)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.primary)
                            .align(Alignment.CenterStart),
                )
            }
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, start = 8.dp, end = 8.dp),
            ) {
                PostHeader(
                    avatar = post.author.avatar?.uri,
                    displayName = post.author.displayName,
                    handle = post.author.handle.handle,
                    indexedAt = post.indexedAt,
                )
                PostMessageText(
                    text = post.getPostText(),
                    onClick = { onPostClick(post.uri.atUri.encodeURLParameter()) },
                )

                post.embed?.let { embed ->
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
                            EmbedRecordView(
                                record = embed.value.record,
                                onMediaOpen = { onMediaOpen(it) },
                            )
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
                                )
                            }
                        }

                        is PostViewEmbedUnion.Unknown -> Unit
                    }
                }

                FeedItemInteractions(
                    onReplyClick = { onReplyClick(post.uri.atUri) },
                    replyCount = post.replyCount,
                    repostCount = post.repostCount,
                    likeCount = post.likeCount,
                )
            }
        }
    }
}
