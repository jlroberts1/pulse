/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.ui.screens.notifications.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.bsky.notification.ListNotificationsNotification
import com.contexts.cosmic.extensions.getRecordText

@Composable
fun NotificationItem(notification: ListNotificationsNotification) {
    ElevatedCard(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(8.dp),
        ) {
            NotificationHeader(
                avatar = notification.author.avatar,
                displayName = notification.author.displayName,
                handle = notification.author.handle.handle,
                reason = notification.reason.value,
                indexedAt = notification.indexedAt,
            )
        }

        if (notification.getRecordText().isNotEmpty()) {
            Text(
                style = MaterialTheme.typography.bodyMedium,
                text = notification.getRecordText(),
                color = Color.Gray,
                modifier = Modifier.padding(8.dp),
            )
        }
    }
}
