/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.ui.screens.profile.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.contexts.cosmic.domain.model.User

@Composable
fun Header(user: User) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
        user.banner?.let { bannerUrl ->
            AsyncImage(
                model = bannerUrl,
                contentDescription = "Profile banner",
                contentScale = ContentScale.FillHeight,
                modifier = Modifier.fillMaxSize(),
            )
        }
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors =
                                listOf(
                                    Color.Transparent,
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.5f),
                                ),
                            startY = 0f,
                            endY = 600f,
                        ),
                    ),
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Spacer(modifier = Modifier.padding(18.dp))
            user.avatar?.let { avatarUrl ->
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = "Profile avatar",
                    modifier =
                        Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                            .align(Alignment.CenterHorizontally),
                    contentScale = ContentScale.Crop,
                )
            }
            Spacer(modifier = Modifier.padding(8.dp))
            Text(
                text = ((user.displayName ?: user.handle).toString()),
                fontWeight = FontWeight.Bold,
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            Text(
                text = "@${user.handle}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }
    }
}
