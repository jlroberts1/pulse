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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.bsky.embed.RecordViewRecordUnion
import app.bsky.feed.PostView
import app.bsky.feed.PostViewEmbedUnion
import app.bsky.feed.ReplyRef
import app.bsky.richtext.Facet
import coil3.compose.AsyncImage
import com.atproto.label.Label
import com.contexts.cosmic.extensions.toRelativeTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import sh.calvin.autolinktext.AutoLinkText
import sh.christian.ozone.api.model.Timestamp

@Composable
fun FeedItem(
    post: PostView,
    onReplyClick: () -> Unit,
    onRepostClick: () -> Unit,
    onLikeClick: () -> Unit,
    onMenuClick: () -> Unit,
) {
    val authorAvatar = post.author.avatar
    val authorName = post.author.displayName
    val authorHandle = post.author.handle
    val indexedAt = post.indexedAt
    val postRecordText = post.getPostText()
    val embed = post.embed
    val replyCount = post.replyCount
    val likeCount = post.likeCount
    val repostCount = post.repostCount

    ElevatedCard(
        modifier = Modifier.padding(8.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AsyncImage(
                    model = authorAvatar?.uri,
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
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        if (!authorName.isNullOrEmpty()) {
                            Text(
                                text = authorName,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Text(
                            text = "@${authorHandle.handle}",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = indexedAt.toRelativeTime(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    )
                }
            }
            AutoLinkText(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp),
                text = postRecordText,
                style =
                    MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
            )
            embed?.let { embed ->
                when (embed) {
                    is PostViewEmbedUnion.RecordWithMediaView -> {
                    }

                    is PostViewEmbedUnion.RecordView -> {
                        EmbedRecordView(embed.value.record)
                    }

                    is PostViewEmbedUnion.VideoView -> {
                        EmbedVideoView(
                            embed.value.thumbnail,
                            embed.value.playlist,
                        )
                    }

                    is PostViewEmbedUnion.ExternalView -> {
                        EmbedExternalView(
                            embed.value.external.uri,
                            embed.value.external.thumb,
                            embed.value.external.title,
                            embed.value.external.description,
                        )
                    }

                    is PostViewEmbedUnion.ImagesView -> {
                        EmbedImageView(embed.value.images)
                    }

                    is PostViewEmbedUnion.Unknown -> {
                    }
                }
            }
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = onReplyClick,
                    modifier = Modifier.weight(1f),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            modifier = Modifier.size(16.dp),
                            imageVector = Icons.AutoMirrored.Filled.Reply,
                            contentDescription = "Reply",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        if (replyCount != null) {
                            if (replyCount > 0) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = replyCount.toString(),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
                IconButton(
                    onClick = onRepostClick,
                    modifier = Modifier.weight(1f),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            modifier = Modifier.size(16.dp),
                            imageVector = Icons.Default.Repeat,
                            contentDescription = "Repost",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        if (repostCount != null) {
                            if (repostCount > 0) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = repostCount.toString(),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
                IconButton(
                    onClick = onLikeClick,
                    modifier = Modifier.weight(1f),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            modifier = Modifier.size(16.dp),
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Like",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        if (likeCount != null) {
                            if (likeCount > 0) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = likeCount.toString(),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Serializable
data class PostRecord(
    val text: String,
    val facets: List<Facet>? = null,
    val reply: ReplyRef? = null,
    val embed: JsonElement? = null,
    val langs: List<String>? = null,
    val labels: List<Label>? = null,
    val createdAt: Timestamp,
)

fun PostView.getPostText(): String {
    return try {
        record.decodeAs<PostRecord>().text
    } catch (e: Exception) {
        ""
    }
}

@Serializable
data class RecordText(
    val text: String,
    val facets: List<Facet>? = null,
    val langs: List<String>? = null,
    val labels: List<Label>? = null,
    val createdAt: Timestamp,
)

fun RecordViewRecordUnion.ViewRecord.getRecordText(): String {
    return try {
        value.value.decodeAs<RecordText>().text
    } catch (e: Exception) {
        ""
    }
}
