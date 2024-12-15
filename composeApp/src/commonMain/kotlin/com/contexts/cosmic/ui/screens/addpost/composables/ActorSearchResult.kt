/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.ui.screens.addpost.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.bsky.actor.ProfileViewBasic
import com.contexts.cosmic.ui.composables.BorderedCircularAvatar

@Composable
fun ActorSearchResult(
    actor: ProfileViewBasic,
    onClick: (ProfileViewBasic) -> Unit,
) {
    Row(
        modifier =
            Modifier.padding(vertical = 8.dp)
                .clickable { onClick(actor) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BorderedCircularAvatar(
            imageUri = actor.avatar?.uri,
            size = 32.dp,
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Text(
            modifier = Modifier.weight(1f),
            text = actor.displayName ?: "",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Text(
            modifier = Modifier.weight(1f),
            text = actor.handle.handle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
        )
    }
}
