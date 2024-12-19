/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.screens.notifications.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.contexts.pulse.extensions.toRelativeTime
import com.contexts.pulse.ui.composables.BadgedAvatar
import sh.christian.ozone.api.Uri
import sh.christian.ozone.api.model.Timestamp

@Composable
fun NotificationHeader(
    avatar: Uri? = null,
    displayName: String? = null,
    handle: String? = null,
    reason: String? = null,
    indexedAt: Timestamp? = null,
) {
    val icon =
        when (reason) {
            "like" -> Icons.Default.Favorite
            "repost" -> Icons.Default.Repeat
            "follow" -> Icons.Default.Person
            "reply" -> Icons.AutoMirrored.Filled.Chat
            "quote" -> Icons.Default.FormatQuote
            else -> null
        }

    val message =
        when (reason) {
            "like" -> "liked your post"
            "repost" -> "reposted your post"
            "follow" -> "followed you"
            "reply" -> "replied to your post"
            "quote" -> "quoted your post"
            else -> ""
        }

    val badgeTint =
        when (reason) {
            "like" -> Color.Red
            "repost" -> Color.Green
            "follow" -> Color.Blue
            "reply" -> Color.Magenta
            "quote" -> Color.Yellow
            else -> LocalContentColor.current
        }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BadgedAvatar(avatar, icon, badgeTint, Modifier.padding(8.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                val name = displayName.takeIf { !it.isNullOrEmpty() } ?: handle
                Text(
                    text = name ?: "",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = message,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = indexedAt?.toRelativeTime() ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            )
        }
    }
}
