/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.contexts.cosmic.domain.model.EmbedView
import kotlinx.serialization.json.jsonPrimitive

@Composable
fun EmbedRecordView(embed: EmbedView) {
    val record = (embed as EmbedView.Record).record
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
                    model = record.author?.avatar,
                    contentDescription = null,
                    modifier =
                        Modifier
                            .size(24.dp)
                            .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
                Text(
                    text =
                        record.author?.displayName
                            ?: "Unknown",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = "@${record.author?.handle}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            record.value?.get("text")?.jsonPrimitive?.content?.let { text ->
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            record.embeds?.firstOrNull()
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
