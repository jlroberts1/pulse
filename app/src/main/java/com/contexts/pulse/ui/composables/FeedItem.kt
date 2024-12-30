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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.contexts.pulse.domain.model.TimelinePost
import com.contexts.pulse.domain.model.TimelinePostFeature

@Composable
fun FeedItem(
    post: TimelinePost,
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
                avatar = post.author.avatar,
                displayName = post.author.displayName,
                handle = post.author.handle.handle,
                indexedAt = post.indexedAt,
            )
            PostMessageText(
                text = post.text,
                onClick = { onPostClick(post.uri.atUri) },
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
                        EmbedRecordView(embedPost = feature.post)
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
