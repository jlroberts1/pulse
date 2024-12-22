/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.ui.screens.addpost.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Gif
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddPostBottomBar(
    launchImageGallery: () -> Unit,
    launchVideoGallery: () -> Unit,
    launchGif: () -> Unit,
    sendPost: () -> Unit,
    modifier: Modifier,
) {
    BottomAppBar(
        modifier = modifier,
        actions = {
            IconButton(onClick = { launchImageGallery() }) {
                Icon(Icons.Default.Photo, "Images")
            }
            IconButton(onClick = { launchVideoGallery() }) {
                Icon(Icons.Default.VideoLibrary, "Videos")
            }
            IconButton(onClick = { launchGif() }) {
                Icon(Icons.Default.Gif, "Gif")
            }
            Spacer(modifier = Modifier.weight(1f))
            FloatingActionButton(
                onClick = { sendPost() },
                modifier =
                    Modifier
                        .padding(8.dp),
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, "Send")
            }
        },
    )
}
