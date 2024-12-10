package com.contexts.cosmic.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.contexts.cosmic.domain.model.EmbedView
import com.contexts.cosmic.domain.model.FeedViewPost
import com.contexts.cosmic.extensions.toRelativeTime
import kotlinx.serialization.json.jsonPrimitive
import sh.calvin.autolinktext.AutoLinkText

@Composable
fun ProfileFeed(feedPost: FeedViewPost) {
    ElevatedCard(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AsyncImage(
                    model = feedPost.post.author.avatar,
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

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = feedPost.post.author.displayName ?: "",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = feedPost.post.author.handle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = feedPost.post.indexedAt.toRelativeTime(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    AutoLinkText(
                        text = feedPost.post.record.text,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(modifier = Modifier.padding(4.dp))
                    feedPost.post.embed?.let { embed ->
                        when (embed) {
                            is EmbedView.Record -> {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    border =
                                        BorderStroke(
                                            1.dp,
                                            MaterialTheme.colorScheme.outlineVariant,
                                        ),
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement =
                                            Arrangement.spacedBy(
                                                8.dp,
                                            ),
                                    ) {
                                        Row(
                                            horizontalArrangement =
                                                Arrangement.spacedBy(
                                                    8.dp,
                                                ),
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            AsyncImage(
                                                model = embed.record.author?.avatar,
                                                contentDescription = null,
                                                modifier =
                                                    Modifier
                                                        .size(24.dp)
                                                        .clip(CircleShape),
                                                contentScale = ContentScale.Crop,
                                            )
                                            Text(
                                                text =
                                                    embed.record.author?.displayName
                                                        ?: "Unknown",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Medium,
                                            )
                                            Text(
                                                text = "@${embed.record.author?.handle}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        }

                                        embed.record.value?.get("text")?.jsonPrimitive?.content?.let { text ->
                                            Text(
                                                text = text,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        }

                                        embed.record.embeds?.firstOrNull()
                                            ?.let { nestedEmbed ->
                                                when (nestedEmbed) {
                                                    is EmbedView.Images -> {
                                                        LazyRow(
                                                            horizontalArrangement =
                                                                Arrangement.spacedBy(
                                                                    8.dp,
                                                                ),
                                                        ) {
                                                            items(nestedEmbed.images.size) {
                                                                nestedEmbed.images.forEach { image ->
                                                                    AsyncImage(
                                                                        model = image.fullsize,
                                                                        contentDescription = image.alt,
                                                                        modifier =
                                                                            Modifier
                                                                                .height(
                                                                                    150.dp,
                                                                                )
                                                                                .aspectRatio(
                                                                                    image.aspectRatio?.width?.toFloat()
                                                                                        ?.div(
                                                                                            image.aspectRatio.height,
                                                                                        )
                                                                                        ?: 1f,
                                                                                )
                                                                                .clip(
                                                                                    RoundedCornerShape(
                                                                                        4.dp,
                                                                                    ),
                                                                                ),
                                                                        contentScale = ContentScale.Crop,
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }

                                                    else -> {}
                                                }
                                            }
                                    }
                                }
                            }

                            is EmbedView.External -> {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    border =
                                        BorderStroke(
                                            1.dp,
                                            MaterialTheme.colorScheme.outlineVariant,
                                        ),
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalArrangement =
                                            Arrangement.spacedBy(
                                                8.dp,
                                            ),
                                    ) {
                                        embed.external.thumb?.let { thumb ->
                                            AsyncImage(
                                                model = thumb,
                                                contentDescription = null,
                                                modifier =
                                                    Modifier
                                                        .size(80.dp)
                                                        .clip(RoundedCornerShape(4.dp)),
                                                contentScale = ContentScale.Crop,
                                            )
                                        }
                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement =
                                                Arrangement.spacedBy(
                                                    4.dp,
                                                ),
                                        ) {
                                            Text(
                                                text = embed.external.title,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Medium,
                                            )
                                            Text(
                                                text =
                                                    embed.external.description
                                                        ?: "",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis,
                                            )
                                            Text(
                                                text = embed.external.uri,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.primary,
                                            )
                                        }
                                    }
                                }
                            }

                            is EmbedView.Images -> {
                                LazyRow(
                                    horizontalArrangement =
                                        Arrangement.spacedBy(
                                            8.dp,
                                        ),
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    items(embed.images.size) {
                                        embed.images.forEach { image ->
                                            AsyncImage(
                                                model =
                                                    image.fullsize.also {
                                                        println(it)
                                                    },
                                                contentDescription = image.alt,
                                                modifier =
                                                    Modifier
                                                        .height(200.dp)
                                                        .aspectRatio(
                                                            image.aspectRatio?.width?.toFloat()
                                                                ?.div(image.aspectRatio.height)
                                                                ?: 1f,
                                                        )
                                                        .clip(RoundedCornerShape(8.dp)),
                                                contentScale = ContentScale.Crop,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
