/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.ui.composables

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import chat.bsky.convo.ConvoView
import chat.bsky.convo.ConvoViewLastMessageUnion

@Composable
fun LastMessageView(conversation: ConvoView) {
    conversation.lastMessage?.let { lastMessage ->
        when (lastMessage) {
            is ConvoViewLastMessageUnion.MessageView -> {
                Text(
                    text = lastMessage.value.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            is ConvoViewLastMessageUnion.DeletedMessageView -> {
                Text(
                    text = "Message deleted",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontStyle = FontStyle.Italic,
                )
            }

            is ConvoViewLastMessageUnion.Unknown -> {
                Text(
                    text = "Unknown error",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontStyle = FontStyle.Italic,
                )
            }
        }
    }
}
