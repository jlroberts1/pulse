/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.bsky.embed.RecordViewRecordEmbedUnion
import app.bsky.embed.RecordViewRecordUnion
import com.contexts.pulse.extensions.getRecordText

@Composable
fun EmbeddedRecordView(
    record: RecordViewRecordUnion.ViewRecord,
    modifier: Modifier = Modifier,
    onMediaOpen: (String) -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        border =
            BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant,
            ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PostHeader(
                avatar = record.value.author.avatar?.uri,
                displayName = record.value.author.displayName,
                handle = record.value.author.handle.handle,
                indexedAt = record.value.indexedAt,
            )

            PostMessageText(
                text = record.getRecordText(),
                onClick = { },
            )

            record.value.embeds.forEach { embed ->
                when (embed) {
                    is RecordViewRecordEmbedUnion.ExternalView -> {
                        EmbedExternalView(
                            uri = embed.value.external.uri,
                            thumb = embed.value.external.thumb,
                            title = embed.value.external.title,
                            description = embed.value.external.description,
                            onMediaOpen = { onMediaOpen(it) },
                        )
                    }
                    is RecordViewRecordEmbedUnion.ImagesView -> {
                        EmbedImagesViewImage(
                            images = embed.value.images,
                            onMediaOpen = { onMediaOpen(it) },
                        )
                    }
                    is RecordViewRecordEmbedUnion.RecordView -> {
                        EmbedRecordView(
                            record = embed.value.record,
                            onMediaOpen = { onMediaOpen(it) },
                        )
                    }
                    is RecordViewRecordEmbedUnion.RecordWithMediaView -> {}
                    is RecordViewRecordEmbedUnion.VideoView -> {
                        EmbedVideoView(
                            thumbnail = embed.value.thumbnail,
                            playlist = embed.value.playlist,
                            aspectRatio = embed.value.aspectRatio,
                            onMediaOpen = { onMediaOpen(it) },
                        )
                    }
                }
            }
        }
    }
}
