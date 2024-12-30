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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.contexts.pulse.domain.model.TimelinePost
import com.contexts.pulse.domain.model.TimelinePostFeature
import com.contexts.pulse.ui.composables.EmbedExternalView
import com.contexts.pulse.ui.composables.EmbedImagesViewImage
import com.contexts.pulse.ui.composables.EmbedRecordView
import com.contexts.pulse.ui.composables.EmbedVideoView
import com.contexts.pulse.ui.composables.FeedItemInteractions
import com.contexts.pulse.ui.composables.PostHeader
import com.contexts.pulse.ui.composables.PostMessageText
import io.ktor.http.encodeURLParameter

@Composable
fun PostViewItem(
    post: TimelinePost,
    isReply: Boolean = false,
    onPostClick: (String) -> Unit,
    onReplyClick: (String) -> Unit,
    onRepostClick: () -> Unit,
    onLikeClick: () -> Unit,
    onMenuClick: () -> Unit,
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
                    avatar = post.author.avatar,
                    displayName = post.author.displayName,
                    handle = post.author.handle.handle,
                    indexedAt = post.indexedAt,
                )
                PostMessageText(
                    text = post.text,
                    onClick = { onPostClick(post.uri.atUri.encodeURLParameter()) },
                )

                post.feature?.let { feature ->
                    when (feature) {
                        is TimelinePostFeature.ExternalFeature -> {
                            EmbedExternalView(
                                uri = feature.uri,
                                thumb = feature.thumb,
                                title = feature.title,
                                onMediaOpen = { onMediaOpen(it) },
                            )
                        }
                        is TimelinePostFeature.ImagesFeature -> {
                            EmbedImagesViewImage(
                                images = feature.images,
                                onMediaOpen = { onMediaOpen(it) },
                            )
                        }
                        is TimelinePostFeature.MediaPostFeature -> Unit
                        is TimelinePostFeature.PostFeature -> {
                            EmbedRecordView(
                                embedPost = feature.post,
                            )
                        }
                        is TimelinePostFeature.VideoFeature -> {
                            EmbedVideoView(
                                thumbnail = feature.video.thumb,
                                playlist = feature.video.playlist,
                                aspectRatio = feature.video.aspectRatio,
                                onMediaOpen = { onMediaOpen(it) },
                            )
                        }
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
