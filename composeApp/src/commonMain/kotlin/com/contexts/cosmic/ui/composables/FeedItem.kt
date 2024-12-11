/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.ui.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.contexts.cosmic.domain.model.EmbedView
import com.contexts.cosmic.extensions.toRelativeTime
import org.jetbrains.compose.ui.tooling.preview.Preview
import sh.calvin.autolinktext.AutoLinkText

@Composable
fun FeedItem(
    authorAvatar: String? = "",
    authorName: String? = "",
    authorHandle: String = "",
    indexedAt: String = "",
    postRecordText: String = "",
    embedView: EmbedView? = null,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AsyncImage(
                model = authorAvatar,
                contentDescription = "Profile avatar",
                modifier =
                    Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .border(
                            2.dp,
                            MaterialTheme.colorScheme.surface,
                            CircleShape,
                        ),
                contentScale = ContentScale.Crop,
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = authorName ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = authorHandle,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = indexedAt.toRelativeTime(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                )
            }
            AutoLinkText(
                text = postRecordText,
                style =
                    MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
            )
            Spacer(modifier = Modifier.padding(4.dp))
            embedView?.let { embed ->
                when (embed) {
                    is EmbedView.RecordWithMedia -> {
                    }

                    is EmbedView.Video -> {
                        EmbedVideoView(
                            embed.thumbnail,
                            embed.playlist,
                        )
                    }

                    is EmbedView.Record -> {
                        EmbedRecordView(embed)
                    }

                    is EmbedView.External -> {
                        EmbedExternalView(
                            embed.external.uri,
                            embed.external.thumb,
                            embed.external.title,
                            embed.external.description,
                        )
                    }

                    is EmbedView.Images -> {
                        EmbedImageView(embed.images)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun FeedPostPreview() {
    FeedItem(
        authorAvatar = "https://i.pravatar.cc/300",
        authorName = "Bob Barkley",
        authorHandle = "@bob.bsky.social",
        indexedAt = "2024-12-11T15:30:45.123Z",
        postRecordText = "Test description for the preview that wraps to the next line on mobile",
    )
}
