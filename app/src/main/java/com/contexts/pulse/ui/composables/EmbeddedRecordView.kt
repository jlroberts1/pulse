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

            record.value.embeds.forEach {
                when (it) {
                    is RecordViewRecordEmbedUnion.ExternalView -> {}
                    is RecordViewRecordEmbedUnion.ImagesView -> {
                        EmbedImagesViewImage(
                            images = it.value.images,
                            onMediaOpen = {},
                        )
                    }
                    is RecordViewRecordEmbedUnion.RecordView -> {}
                    is RecordViewRecordEmbedUnion.RecordWithMediaView -> {}
                    is RecordViewRecordEmbedUnion.Unknown -> {}
                    is RecordViewRecordEmbedUnion.VideoView -> {}
                }
            }
        }
    }
}
