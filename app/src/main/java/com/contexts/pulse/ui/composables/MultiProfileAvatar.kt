/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import chat.bsky.actor.ProfileViewBasic
import chat.bsky.convo.ConvoView
import coil3.compose.AsyncImage

@Composable
fun MultiProfileAvatar(
    members: List<ProfileViewBasic>,
    conversation: ConvoView,
) {
    when (members.size) {
        1 -> {
            AsyncImage(
                model = members.first().avatar?.uri,
                contentDescription = null,
                modifier =
                    Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
        }

        2 -> {
            members.take(2).forEachIndexed { index, member ->
                AsyncImage(
                    model = member.avatar?.uri,
                    contentDescription = null,
                    modifier =
                        Modifier
                            .size(36.dp)
                            .offset(x = (index * 16).dp)
                            .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
            }
        }

        else -> {
            Box(
                modifier =
                    Modifier
                        .size(48.dp)
                        .background(
                            MaterialTheme.colorScheme.secondaryContainer,
                            CircleShape,
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = conversation.members.size.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
        }
    }
}
